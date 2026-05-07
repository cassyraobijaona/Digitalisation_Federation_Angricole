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

    // findOverallStatistics — avec assiduité globale (pushdown)
    public List<CollectivityOverallStatistics> findOverallStatistics(
            LocalDate from, LocalDate to) {

        String sql = """
        SELECT
            c.id,
            c.name,
            c.number,
            COUNT(DISTINCT m.id) AS total_members,

            -- nouveaux adhérents
            COUNT(DISTINCT CASE
                WHEN m.adhesion_date BETWEEN ? AND ?
                THEN m.id END) AS new_members,

            -- membres à jour dans cotisations
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

            -- taux d'assiduité global (pushdown)
            CASE
                WHEN (
                    SELECT COUNT(*)
                    FROM collectivity_activity ca
                    JOIN activity_occupation_concerned aoc ON aoc.activity_id = ca.id
                    JOIN collectivity_member cm2 ON cm2.collectivity_id = c.id
                    JOIN member m2 ON m2.id = cm2.member_id AND m2.occupation = aoc.occupation
                    WHERE ca.collectivity_id = c.id
                      AND ca.executive_date BETWEEN ? AND ?
                ) = 0 THEN 0.0
                ELSE (
                    SELECT COUNT(*) * 100.0 /
                        (SELECT COUNT(*)
                         FROM collectivity_activity ca2
                         JOIN activity_occupation_concerned aoc2 ON aoc2.activity_id = ca2.id
                         JOIN collectivity_member cm3 ON cm3.collectivity_id = c.id
                         JOIN member m3 ON m3.id = cm3.member_id AND m3.occupation = aoc2.occupation
                         WHERE ca2.collectivity_id = c.id
                           AND ca2.executive_date BETWEEN ? AND ?)
                    FROM activity_attendance aa
                    JOIN collectivity_activity ca4 ON ca4.id = aa.activity_id
                    WHERE aa.attendance_status = 'ATTENDED'
                      AND ca4.collectivity_id = c.id
                      AND ca4.executive_date BETWEEN ? AND ?
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

            // nouveaux adhérents
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            // paiements
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            // cotisations ACTIVE (1ère)
            ps.setDate(5, Date.valueOf(from));
            ps.setDate(6, Date.valueOf(to));
            // cotisations ACTIVE (2ème)
            ps.setDate(7, Date.valueOf(from));
            ps.setDate(8, Date.valueOf(to));
            // assiduité — total (CASE WHEN)
            ps.setDate(9, Date.valueOf(from));
            ps.setDate(10, Date.valueOf(to));
            // assiduité — total (sous-requête dénominateur)
            ps.setDate(11, Date.valueOf(from));
            ps.setDate(12, Date.valueOf(to));
            // assiduité — présences
            ps.setDate(13, Date.valueOf(from));
            ps.setDate(14, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CollectivityInformation info = new CollectivityInformation();
                info.setName(rs.getString("name"));
                info.setNumber(rs.getObject("number", Integer.class));

                int total = rs.getInt("total_members");
                int paid = rs.getInt("paid_members");
                double percentage = total > 0 ? (paid * 100.0 / total) : 0.0;

                CollectivityOverallStatistics stat = new CollectivityOverallStatistics();
                stat.setCollectivityInformation(info);
                stat.setNewMembersNumber(rs.getInt("new_members"));
                stat.setOverallMemberCurrentDuePercentage(percentage);
                stat.setOverallMemberAssiduityPercentage(
                        rs.getDouble("assiduity_percentage"));

                result.add(stat);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    // findLocalStatistics — avec assiduité par membre (pushdown)
    public List<CollectivityLocalStatistics> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

        String sql = """
        SELECT
            m.id,
            m.first_name,
            m.last_name,
            m.email,
            m.occupation,

            -- montant encaissé
            COALESCE((
                SELECT SUM(mp.amount)
                FROM member_payment mp
                JOIN membership_fee mf ON mf.id = mp.membership_fee_id
                WHERE mp.member_id = m.id
                  AND mf.collectivity_id = cm.collectivity_id
                  AND mp.creation_date BETWEEN ? AND ?
            ), 0) AS earned_amount,

            -- montant impayé (cotisations ACTIVE uniquement)
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

            -- taux d'assiduité (pushdown)
            CASE
                WHEN (
                    SELECT COUNT(*)
                    FROM collectivity_activity ca
                    JOIN activity_occupation_concerned aoc ON aoc.activity_id = ca.id
                    WHERE ca.collectivity_id = cm.collectivity_id
                      AND ca.executive_date BETWEEN ? AND ?
                      AND aoc.occupation = m.occupation
                ) = 0 THEN 0.0
                ELSE (
                    SELECT COUNT(*) * 100.0 /
                        (SELECT COUNT(*)
                         FROM collectivity_activity ca2
                         JOIN activity_occupation_concerned aoc2 ON aoc2.activity_id = ca2.id
                         WHERE ca2.collectivity_id = cm.collectivity_id
                           AND ca2.executive_date BETWEEN ? AND ?
                           AND aoc2.occupation = m.occupation)
                    FROM activity_attendance aa
                    JOIN collectivity_activity ca3 ON ca3.id = aa.activity_id
                    JOIN activity_occupation_concerned aoc3 ON aoc3.activity_id = ca3.id
                    WHERE aa.member_id = m.id
                      AND aa.attendance_status = 'ATTENDED'
                      AND ca3.collectivity_id = cm.collectivity_id
                      AND ca3.executive_date BETWEEN ? AND ?
                      AND aoc3.occupation = m.occupation
                )
            END AS assiduity_percentage

        FROM member m
        JOIN collectivity_member cm ON cm.member_id = m.id
        WHERE cm.collectivity_id = ?
    """;

        List<CollectivityLocalStatistics> result = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            // earned_amount
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            // unpaid_amount — cotisations ACTIVE
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            // unpaid_amount — paiements
            ps.setDate(5, Date.valueOf(from));
            ps.setDate(6, Date.valueOf(to));
            // assiduité — total activités concernées
            ps.setDate(7, Date.valueOf(from));
            ps.setDate(8, Date.valueOf(to));
            // assiduité — total activités concernées (2ème sous-requête)
            ps.setDate(9, Date.valueOf(from));
            ps.setDate(10, Date.valueOf(to));
            // assiduité — présences
            ps.setDate(11, Date.valueOf(from));
            ps.setDate(12, Date.valueOf(to));
            // collectivity_id
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
    private double getEarnedAmount(Connection conn, String memberId,
                                   String collectivityId, LocalDate from, LocalDate to) throws SQLException {

        String sql = """
            SELECT COALESCE(SUM(mp.amount), 0) as total
            FROM member_payment mp
            JOIN membership_fee mf ON mf.id = mp.membership_fee_id
            WHERE mp.member_id = ?
            AND mf.collectivity_id = ?
            AND mp.creation_date BETWEEN ? AND ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setString(2, collectivityId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble("total");
        }
    }

    private double getUnpaidAmount(Connection conn,
                                   String memberId,
                                   String collectivityId,
                                   LocalDate from,
                                   LocalDate to) throws SQLException {

        String sql = """
        SELECT GREATEST(
            (
                SELECT COALESCE(SUM(mf.amount), 0)
                FROM membership_fee mf
                WHERE mf.collectivity_id = ?
                AND mf.status = 'ACTIVE'
                AND mf.eligible_from <= ?
            )
            -
            (
                SELECT COALESCE(SUM(mp.amount), 0)
                FROM member_payment mp
                JOIN membership_fee mf
                    ON mf.id = mp.membership_fee_id
                WHERE mp.member_id = ?
                AND mf.collectivity_id = ?
                AND mf.status = 'ACTIVE'
                AND mp.creation_date BETWEEN ? AND ?
            ),
            0
        ) AS unpaid_amount
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(to));

            ps.setString(3, memberId);
            ps.setString(4, collectivityId);
            ps.setDate(5, Date.valueOf(from));
            ps.setDate(6, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("unpaid_amount");
            }
        }

        return 0;
    }
}

