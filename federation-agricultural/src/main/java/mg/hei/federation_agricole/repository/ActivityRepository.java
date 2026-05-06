package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ActivityRepository {

    private final DatabaseConnection db;

    public ActivityRepository(DatabaseConnection db) {
        this.db = db;
    }

    public List<CollectivityActivity> saveAll(
            String collectivityId,
            List<CreateCollectivityActivity> activities) throws SQLException {

        List<CollectivityActivity> result = new ArrayList<>();

        String sql = """
            INSERT INTO collectivity_activity
            (id, collectivity_id, label, activity_type,
             member_occupation_concerned,
             recurrence_week_ordinal, recurrence_day_of_week,
             executive_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = db.getConnection()) {
            for (CreateCollectivityActivity a : activities) {

                // Validation : pas les deux en même temps
                if (a.getRecurrenceRule() != null && a.getExecutiveDate() != null) {
                    throw new RuntimeException(
                            "Cannot provide both recurrenceRule and executiveDate");
                }

                String id = UUID.randomUUID().toString();
                String occupations = a.getMemberOccupationConcerned() != null
                        ? String.join(",", a.getMemberOccupationConcerned())
                        : null;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, id);
                    ps.setString(2, collectivityId);
                    ps.setString(3, a.getLabel());
                    ps.setString(4, a.getActivityType());
                    ps.setString(5, occupations);

                    if (a.getRecurrenceRule() != null) {
                        ps.setInt(6, a.getRecurrenceRule().getWeekOrdinal());
                        ps.setString(7, a.getRecurrenceRule().getDayOfWeek());
                    } else {
                        ps.setNull(6, Types.INTEGER);
                        ps.setNull(7, Types.VARCHAR);
                    }

                    if (a.getExecutiveDate() != null) {
                        ps.setDate(8, Date.valueOf(a.getExecutiveDate()));
                    } else {
                        ps.setNull(8, Types.DATE);
                    }

                    ps.executeUpdate();
                }

                CollectivityActivity created = new CollectivityActivity();
                created.setId(id);
                created.setLabel(a.getLabel());
                created.setActivityType(a.getActivityType());
                created.setMemberOccupationConcerned(a.getMemberOccupationConcerned());
                created.setRecurrenceRule(a.getRecurrenceRule());
                created.setExecutiveDate(a.getExecutiveDate());
                result.add(created);
            }
        }
        return result;
    }

    public List<CollectivityActivity> findByCollectivity(
            String collectivityId) throws SQLException {

        List<CollectivityActivity> result = new ArrayList<>();

        String sql = """
            SELECT id, label, activity_type,
                   member_occupation_concerned,
                   recurrence_week_ordinal, recurrence_day_of_week,
                   executive_date
            FROM collectivity_activity
            WHERE collectivity_id = ?
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CollectivityActivity a = new CollectivityActivity();
                a.setId(rs.getString("id"));
                a.setLabel(rs.getString("label"));
                a.setActivityType(rs.getString("activity_type"));

                String occ = rs.getString("member_occupation_concerned");
                if (occ != null && !occ.isEmpty()) {
                    a.setMemberOccupationConcerned(List.of(occ.split(",")));
                }

                Integer weekOrdinal = (Integer) rs.getObject("recurrence_week_ordinal");
                String dayOfWeek = rs.getString("recurrence_day_of_week");
                if (weekOrdinal != null && dayOfWeek != null) {
                    MonthlyRecurrenceRule rule = new MonthlyRecurrenceRule();
                    rule.setWeekOrdinal(weekOrdinal);
                    rule.setDayOfWeek(dayOfWeek);
                    a.setRecurrenceRule(rule);
                }

                Date execDate = rs.getDate("executive_date");
                if (execDate != null) {
                    a.setExecutiveDate(execDate.toLocalDate());
                }

                result.add(a);
            }
        }
        return result;
    }

    public List<ActivityMemberAttendance> saveAttendance(
            String activityId,
            List<CreateActivityMemberAttendance> attendances) throws SQLException {

        List<ActivityMemberAttendance> result = new ArrayList<>();

        // Vérifier que l'activité existe
        String checkSql = "SELECT id FROM collectivity_activity WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, activityId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new RuntimeException("Activity not found");
        }

        String sql = """
            INSERT INTO activity_attendance (id, activity_id, member_id, attendance_status)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (activity_id, member_id) DO NOTHING
        """;

        String checkExistingSql = """
            SELECT attendance_status FROM activity_attendance
            WHERE activity_id = ? AND member_id = ?
        """;

        try (Connection conn = db.getConnection()) {
            for (CreateActivityMemberAttendance a : attendances) {

                // Vérifier si déjà marqué ATTENDED ou MISSING → erreur
                try (PreparedStatement ps = conn.prepareStatement(checkExistingSql)) {
                    ps.setString(1, activityId);
                    ps.setString(2, a.getMemberIdentifier());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String existing = rs.getString("attendance_status");
                        if ("ATTENDED".equals(existing) || "MISSING".equals(existing)) {
                            throw new RuntimeException(
                                    "Attendance already confirmed for member: "
                                            + a.getMemberIdentifier());
                        }
                    }
                }

                String id = UUID.randomUUID().toString();
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, id);
                    ps.setString(2, activityId);
                    ps.setString(3, a.getMemberIdentifier());
                    ps.setString(4, a.getAttendanceStatus());
                    ps.executeUpdate();
                }

                // Récupérer le membre
                ActivityMemberAttendance att = new ActivityMemberAttendance();
                att.setId(id);
                att.setAttendanceStatus(a.getAttendanceStatus());

                MemberDescription desc = getMemberDescription(conn, a.getMemberIdentifier());
                att.setMemberDescription(desc);
                result.add(att);
            }
        }
        return result;
    }

    public List<ActivityMemberAttendance> findAttendance(
            String activityId) throws SQLException {

        List<ActivityMemberAttendance> result = new ArrayList<>();

        String sql = """
            SELECT aa.id, aa.attendance_status,
                   m.id as member_id, m.first_name, m.last_name,
                   m.email, m.occupation
            FROM activity_attendance aa
            JOIN member m ON m.id = aa.member_id
            WHERE aa.activity_id = ?
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, activityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ActivityMemberAttendance att = new ActivityMemberAttendance();
                att.setId(rs.getString("id"));
                att.setAttendanceStatus(rs.getString("attendance_status"));

                MemberDescription desc = new MemberDescription();
                desc.setId(rs.getString("member_id"));
                desc.setFirstName(rs.getString("first_name"));
                desc.setLastName(rs.getString("last_name"));
                desc.setEmail(rs.getString("email"));
                desc.setOccupation(rs.getString("occupation"));

                att.setMemberDescription(desc);
                result.add(att);
            }
        }
        return result;
    }

    private MemberDescription getMemberDescription(
            Connection conn, String memberId) throws SQLException {

        String sql = """
            SELECT id, first_name, last_name, email, occupation
            FROM member WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MemberDescription desc = new MemberDescription();
                desc.setId(rs.getString("id"));
                desc.setFirstName(rs.getString("first_name"));
                desc.setLastName(rs.getString("last_name"));
                desc.setEmail(rs.getString("email"));
                desc.setOccupation(rs.getString("occupation"));
                return desc;
            }
        }
        return null;
    }
}