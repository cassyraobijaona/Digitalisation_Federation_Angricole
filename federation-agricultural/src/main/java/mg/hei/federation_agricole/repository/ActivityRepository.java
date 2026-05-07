package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.model.enums.ActivityType;
import mg.hei.federation_agricole.model.enums.AttendanceStatus;
import mg.hei.federation_agricole.model.enums.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Repository
public class ActivityRepository {
    private final DatabaseConnection db;

    public ActivityRepository(DatabaseConnection db) {
        this.db = db;
    }

    public CollectivityActivity save(String collectivityId,
                                     CreateCollectivityActivity input) {
        String sql = """
        INSERT INTO collectivity_activity(id, collectivity_id, label, activity_type, executive_date, week_ordinal, day_of_week)
        VALUES (?, ?, ?, ?::activity_type_enum, ?, ?, ?::day_of_week_enum)
    """;
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            String activityId = input.getId();
            if (activityId == null || activityId.isBlank()) {
                activityId = java.util.UUID.randomUUID().toString();
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, activityId);
            ps.setString(2, collectivityId);
            ps.setString(3, input.getLabel());
            ps.setString(4, input.getActivityType().name());

            if (input.getExecutiveDate() != null) {
                ps.setDate(5, Date.valueOf(input.getExecutiveDate()));
                ps.setNull(6, Types.INTEGER);
                ps.setNull(7, Types.VARCHAR);
            } else {
                ps.setNull(5, Types.DATE);
                ps.setInt(6, input.getRecurrenceRule().getWeekOrdinal());
                ps.setString(7, input.getRecurrenceRule().getDayOfWeek());
            }

            ps.executeUpdate();

            // Insérer les occupations concernées
            if (input.getMemberOccupationConcerned() != null && !input.getMemberOccupationConcerned().isEmpty()) {
                String sqlOcc = "INSERT INTO activity_occupation_concerned(activity_id, occupation) VALUES (?, ?::occupation_enum)";
                for (MemberOccupation occ : input.getMemberOccupationConcerned()) {
                    try (PreparedStatement psOcc = conn.prepareStatement(sqlOcc)) {
                        psOcc.setString(1, activityId);
                        psOcc.setString(2, occ.name());
                        psOcc.executeUpdate();
                    }
                }
            }

            conn.commit();

            // Construire l'objet de retour directement
            CollectivityActivity result = new CollectivityActivity();
            result.setId(activityId);
            result.setLabel(input.getLabel());
            result.setActivityType(input.getActivityType());
            result.setMemberOccupationConcerned(input.getMemberOccupationConcerned());

            if (input.getExecutiveDate() != null) {
                result.setExecutiveDate(input.getExecutiveDate());
            } else {
                result.setRecurrenceRule(input.getRecurrenceRule());
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save activity: " + e.getMessage(), e);
        }
    }
    public List<CollectivityActivity> findByCollectivity(String collectivityId) {
        String sql = """
            SELECT ca.id, ca.label, ca.activity_type, ca.executive_date,
                   ca.week_ordinal, ca.day_of_week
            FROM collectivity_activity ca
            WHERE ca.collectivity_id = ?
        """;

        List<CollectivityActivity> result = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CollectivityActivity a = mapActivity(conn, rs);
                result.add(a);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    public CollectivityActivity findById(Connection conn, String id) throws SQLException {
        String sql = """
        SELECT id, label, activity_type, executive_date, week_ordinal, day_of_week
        FROM collectivity_activity WHERE id = ?
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapActivity(conn, rs);
                }
            }
        }
        return null;
    }
    private CollectivityActivity mapActivity(Connection conn, ResultSet rs) throws SQLException {
        CollectivityActivity a = new CollectivityActivity();
        a.setId(rs.getString("id"));
        a.setLabel(rs.getString("label"));
        a.setActivityType(ActivityType.valueOf(rs.getString("activity_type")));

        Date execDate = rs.getDate("executive_date");
        if (execDate != null) {
            a.setExecutiveDate(execDate.toLocalDate());
        }

        int weekOrdinal = rs.getInt("week_ordinal");
        String dayOfWeek = rs.getString("day_of_week");
        if (!rs.wasNull() && dayOfWeek != null) {
            MonthlyRecurrenceRule rule = new MonthlyRecurrenceRule();
            rule.setWeekOrdinal(weekOrdinal);
            rule.setDayOfWeek(dayOfWeek);
            a.setRecurrenceRule(rule);
        }

        // Occupations concernées
        String sqlOcc = "SELECT occupation FROM activity_occupation_concerned WHERE activity_id = ?";
        PreparedStatement psOcc = conn.prepareStatement(sqlOcc);
        psOcc.setString(1, a.getId());
        ResultSet rsOcc = psOcc.executeQuery();
        List<MemberOccupation> occs = new ArrayList<>();
        while (rsOcc.next()) {
            occs.add(MemberOccupation.valueOf(rsOcc.getString("occupation")));
        }
        a.setMemberOccupationConcerned(occs);

        return a;
    }
    public List<ActivityMemberAttendance> saveAttendance(
            String activityId, List<CreateActivityMemberAttendance> list) {

        List<ActivityMemberAttendance> result = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            for (CreateActivityMemberAttendance input : list) {

                // Générer un ID si non fourni
                String attendanceId = input.getId();
                if (attendanceId == null || attendanceId.isBlank()) {
                    attendanceId = java.util.UUID.randomUUID().toString();
                }

                // Vérifie si déjà ATTENDED ou MISSING
                String checkSql = """
                SELECT attendance_status FROM activity_attendance
                WHERE activity_id = ? AND member_id = ?
            """;
                PreparedStatement check = conn.prepareStatement(checkSql);
                check.setString(1, activityId);
                check.setString(2, input.getMemberIdentifier());
                ResultSet rsCheck = check.executeQuery();

                if (rsCheck.next()) {
                    String status = rsCheck.getString("attendance_status");
                    if (!status.equals("UNDEFINED")) {
                        throw new RuntimeException(
                                "Attendance already confirmed for member: "
                                        + input.getMemberIdentifier());
                    }
                    // Update si UNDEFINED
                    String updateSql = """
                    UPDATE activity_attendance
                    SET attendance_status = ?::attendance_status_enum
                    WHERE activity_id = ? AND member_id = ?
                """;
                    PreparedStatement update = conn.prepareStatement(updateSql);
                    update.setString(1, input.getAttendanceStatus().name());
                    update.setString(2, activityId);
                    update.setString(3, input.getMemberIdentifier());
                    update.executeUpdate();
                } else {
                    // Insert nouveau
                    String insertSql = """
                    INSERT INTO activity_attendance(id, activity_id, member_id, attendance_status, attendance_date)
                    VALUES (?, ?, ?, ?::attendance_status_enum, CURRENT_DATE)
                """;
                                    PreparedStatement insert = conn.prepareStatement(insertSql);
                    insert.setString(1, attendanceId);
                    insert.setString(2, activityId);
                    insert.setString(3, input.getMemberIdentifier());
                    insert.setString(4, input.getAttendanceStatus().name());
                    insert.executeUpdate();
                }

                // ✅ Récupérer les informations complètes du membre
                MemberDescription desc = getMemberDescription(conn, input.getMemberIdentifier());

                ActivityMemberAttendance att = new ActivityMemberAttendance();
                att.setId(attendanceId);
                att.setMemberDescription(desc);
                att.setAttendanceStatus(input.getAttendanceStatus());
                result.add(att);
            }

            conn.commit();

        } catch (RuntimeException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    // ✅ Méthode utilitaire pour récupérer les infos complètes d'un membre
    private MemberDescription getMemberDescription(Connection conn, String memberId) throws SQLException {
        String sql = """
        SELECT id, first_name, last_name, email, occupation
        FROM member WHERE id = ?
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MemberDescription desc = new MemberDescription();
                    desc.setId(rs.getString("id"));
                    desc.setFirstName(rs.getString("first_name"));
                    desc.setLastName(rs.getString("last_name"));
                    desc.setEmail(rs.getString("email"));
                    desc.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));
                    return desc;
                }
            }
        }
        throw new RuntimeException("Member not found: " + memberId);
    }

    public List<ActivityMemberAttendance> findAttendance(String activityId) {
        String sql = """
            SELECT aa.id, aa.member_id, aa.attendance_status,
                   m.first_name, m.last_name, m.email, m.occupation
            FROM activity_attendance aa
            JOIN member m ON m.id = aa.member_id
            WHERE aa.activity_id = ?
        """;

        List<ActivityMemberAttendance> result = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, activityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MemberDescription desc = new MemberDescription();
                desc.setId(rs.getString("member_id"));
                desc.setFirstName(rs.getString("first_name"));
                desc.setLastName(rs.getString("last_name"));
                desc.setEmail(rs.getString("email"));
                desc.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));

                ActivityMemberAttendance att = new ActivityMemberAttendance();
                att.setId(rs.getString("id"));
                att.setMemberDescription(desc);
                att.setAttendanceStatus(
                        AttendanceStatus.valueOf(rs.getString("attendance_status")));

                result.add(att);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
