package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.CollectivityInformation;
import mg.hei.federation_agricole.model.dto.CollectivityLocalStatistics;
import mg.hei.federation_agricole.model.dto.CollectivityOverallStatistics;
import mg.hei.federation_agricole.model.dto.MemberDescription;
import mg.hei.federation_agricole.model.enums.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StatisticsRepository {

    private final DatabaseConnection db;

    public StatisticsRepository(DatabaseConnection db) {
        this.db = db;
    }

    public List<CollectivityOverallStatistics> findOverallStatistics(
            LocalDate from, LocalDate to) {

        String sql = """
            SELECT
                c.id,
                c.name,
                c.number,
                COUNT(DISTINCT m.id) AS total_members,

                COUNT(DISTINCT CASE
                    WHEN m.adhesion_date BETWEEN ? AND ?
                    THEN m.id END) AS new_members,

                COUNT(DISTINCT CASE
                    WHEN (
                        SELECT COALESCE(SUM(mp.amount), 0)
                        FROM member_payment mp
                        JOIN membership_fee mf ON mf.id = mp.membership_fee_id
                        WHERE mp.member_id = m.id
                          AND mf.collectivity_id = c.id
                          AND mf.status = 'ACTIVE'
                          AND mp.creation_date BETWEEN ? AND ?
                    ) >= (
                        SELECT COALESCE(SUM(mf2.amount), 0)
                        FROM membership_fee mf2
                        WHERE mf2.collectivity_id = c.id
                          AND mf2.status = 'ACTIVE'
                          AND mf2.eligible_from BETWEEN ? AND ?
                    )
                    AND (
                        SELECT COALESCE(SUM(mf2.amount), 0)
                        FROM membership_fee mf2
                        WHERE mf2.collectivity_id = c.id
                          AND mf2.status = 'ACTIVE'
                          AND mf2.eligible_from BETWEEN ? AND ?
                    ) > 0
                    THEN m.id END) AS paid_members,

                -- ✅ assiduité avec attendance_date
                CASE
                    WHEN (
                        SELECT COUNT(*)
                        FROM activity_attendance aa
                        JOIN collectivity_activity ca ON ca.id = aa.activity_id
                        WHERE ca.collectivity_id = c.id
                          AND aa.attendance_date BETWEEN ? AND ?
                    ) = 0 THEN 0.0
                    ELSE (
                        SELECT COUNT(*) * 100.0 / NULLIF((
                            SELECT COUNT(*)
                            FROM activity_attendance aa2
                            JOIN collectivity_activity ca2 ON ca2.id = aa2.activity_id
                            WHERE ca2.collectivity_id = c.id
                              AND aa2.attendance_date BETWEEN ? AND ?
                        ), 0)
                        FROM activity_attendance aa3
                        JOIN collectivity_activity ca3 ON ca3.id = aa3.activity_id
                        WHERE aa3.attendance_status = 'ATTENDED'
                          AND ca3.collectivity_id = c.id
                          AND aa3.attendance_date BETWEEN ? AND ?
                    )
                END AS assiduity_percentage

            FROM collectivity c
            JOIN collectivity_member cm ON cm.collectivity_id = c.id
            JOIN member m ON m.id = cm.member_id
            GROUP BY c.id, c.name, c.number
        """;

        List<CollectivityOverallStatistics> result = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setDate(1,  Date.valueOf(from)); // new_members from
            ps.setDate(2,  Date.valueOf(to));   // new_members to
            ps.setDate(3,  Date.valueOf(from)); // paiements from
            ps.setDate(4,  Date.valueOf(to));   // paiements to
            ps.setDate(5,  Date.valueOf(from)); // cotisations 1 from
            ps.setDate(6,  Date.valueOf(to));   // cotisations 1 to
            ps.setDate(7,  Date.valueOf(from)); // cotisations 2 from
            ps.setDate(8,  Date.valueOf(to));   // cotisations 2 to
            ps.setDate(9,  Date.valueOf(from)); // assiduité CASE WHEN from
            ps.setDate(10, Date.valueOf(to));   // assiduité CASE WHEN to
            ps.setDate(11, Date.valueOf(from)); // assiduité dénominateur from
            ps.setDate(12, Date.valueOf(to));   // assiduité dénominateur to
            ps.setDate(13, Date.valueOf(from)); // assiduité présences from
            ps.setDate(14, Date.valueOf(to));   // assiduité présences to

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CollectivityInformation info = new CollectivityInformation();
                info.setName(rs.getString("name"));
                info.setNumber(rs.getObject("number", Integer.class));

                int total = rs.getInt("total_members");
                int paid  = rs.getInt("paid_members");
                double percentage = total > 0 ? (paid * 100.0 / total) : 0.0;

                CollectivityOverallStatistics stat = new CollectivityOverallStatistics();
                stat.setCollectivityInformation(info);
                stat.setNewMembersNumber(rs.getInt("new_members"));
                stat.setOverallMemberCurrentDuePercentage(percentage);
                stat.setOverallMemberAssiduityPercentage(rs.getDouble("assiduity_percentage"));

                result.add(stat);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public List<CollectivityLocalStatistics> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

        String sql = """
            SELECT
                m.id,
                m.first_name,
                m.last_name,
                m.email,
                m.occupation,

                COALESCE((
                    SELECT SUM(mp.amount)
                    FROM member_payment mp
                    JOIN membership_fee mf ON mf.id = mp.membership_fee_id
                    WHERE mp.member_id = m.id
                      AND mf.collectivity_id = cm.collectivity_id
                      AND mp.creation_date BETWEEN ? AND ?
                ), 0) AS earned_amount,

                GREATEST(0, COALESCE((
                    SELECT SUM(mf2.amount)
                    FROM membership_fee mf2
                    WHERE mf2.collectivity_id = cm.collectivity_id
                      AND mf2.status = 'ACTIVE'
                      AND mf2.eligible_from BETWEEN ? AND ?
                ), 0) - COALESCE((
                    SELECT SUM(mp2.amount)
                    FROM member_payment mp2
                    JOIN membership_fee mf3 ON mf3.id = mp2.membership_fee_id
                    WHERE mp2.member_id = m.id
                      AND mf3.collectivity_id = cm.collectivity_id
                      AND mf3.status = 'ACTIVE'
                      AND mp2.creation_date BETWEEN ? AND ?
                ), 0)) AS unpaid_amount,

                -- ✅ assiduité avec attendance_date
                CASE
                    WHEN (
                        SELECT COUNT(DISTINCT aa2.attendance_date)
                        FROM activity_attendance aa2
                        JOIN collectivity_activity ca2 ON ca2.id = aa2.activity_id
                        JOIN activity_occupation_concerned aoc2 ON aoc2.activity_id = ca2.id
                        WHERE ca2.collectivity_id = cm.collectivity_id
                          AND aoc2.occupation = m.occupation
                          AND aa2.attendance_date BETWEEN ? AND ?
                    ) = 0 THEN 0.0
                    ELSE (
                        SELECT COUNT(*) * 100.0 / NULLIF((
                            SELECT COUNT(DISTINCT aa3.attendance_date)
                            FROM activity_attendance aa3
                            JOIN collectivity_activity ca3 ON ca3.id = aa3.activity_id
                            JOIN activity_occupation_concerned aoc3 ON aoc3.activity_id = ca3.id
                            WHERE ca3.collectivity_id = cm.collectivity_id
                              AND aoc3.occupation = m.occupation
                              AND aa3.attendance_date BETWEEN ? AND ?
                        ), 0)
                        FROM activity_attendance aa4
                        JOIN collectivity_activity ca4 ON ca4.id = aa4.activity_id
                        JOIN activity_occupation_concerned aoc4 ON aoc4.activity_id = ca4.id
                        WHERE aa4.member_id = m.id
                          AND aa4.attendance_status = 'ATTENDED'
                          AND ca4.collectivity_id = cm.collectivity_id
                          AND aoc4.occupation = m.occupation
                          AND aa4.attendance_date BETWEEN ? AND ?
                    )
                END AS assiduity_percentage

            FROM member m
            JOIN collectivity_member cm ON cm.member_id = m.id
            WHERE cm.collectivity_id = ?
        """;

        List<CollectivityLocalStatistics> result = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setDate(1,  Date.valueOf(from)); // earned_amount from
            ps.setDate(2,  Date.valueOf(to));   // earned_amount to
            ps.setDate(3,  Date.valueOf(from)); // unpaid cotisations from
            ps.setDate(4,  Date.valueOf(to));   // unpaid cotisations to
            ps.setDate(5,  Date.valueOf(from)); // unpaid paiements from
            ps.setDate(6,  Date.valueOf(to));   // unpaid paiements to
            ps.setDate(7,  Date.valueOf(from)); // assiduité CASE WHEN from
            ps.setDate(8,  Date.valueOf(to));   // assiduité CASE WHEN to
            ps.setDate(9,  Date.valueOf(from)); // assiduité dénominateur from
            ps.setDate(10, Date.valueOf(to));   // assiduité dénominateur to
            ps.setDate(11, Date.valueOf(from)); // assiduité présences from
            ps.setDate(12, Date.valueOf(to));   // assiduité présences to
            ps.setString(13, collectivityId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MemberDescription desc = new MemberDescription();
                desc.setId(rs.getString("id"));
                desc.setFirstName(rs.getString("first_name"));
                desc.setLastName(rs.getString("last_name"));
                desc.setEmail(rs.getString("email"));
                desc.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));

                CollectivityLocalStatistics stat = new CollectivityLocalStatistics();
                stat.setMemberDescription(desc);
                stat.setEarnedAmount(rs.getDouble("earned_amount"));
                stat.setUnpaidAmount(rs.getDouble("unpaid_amount"));
                stat.setAssiduityPercentage(rs.getDouble("assiduity_percentage"));

                result.add(stat);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}