package mg.hei.federation_agricole.repository;


import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.CollectivityInformation;
import mg.hei.federation_agricole.model.dto.CollectivityOverallStatistics;
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
                -- montant total payé par le membre pour les cotisations ACTIVE
                SELECT COALESCE(SUM(mp.amount), 0)
                FROM member_payment mp
                JOIN membership_fee mf ON mf.id = mp.membership_fee_id
                WHERE mp.member_id = m.id
                  AND mf.collectivity_id = c.id
                  AND mf.status = 'ACTIVE'
                  AND mp.creation_date BETWEEN ? AND ?
            ) >= (
                -- montant total des cotisations ACTIVE de la collectivité
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
            THEN m.id END) AS paid_members
    FROM collectivity c
    JOIN collectivity_member cm ON cm.collectivity_id = c.id
    JOIN member m ON m.id = cm.member_id
    GROUP BY c.id, c.name, c.number
""";

        List<CollectivityOverallStatistics> result = new ArrayList<>();


            try (Connection conn = db.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setDate(1, Date.valueOf(from)); // new_members from
                ps.setDate(2, Date.valueOf(to));   // new_members to
                ps.setDate(3, Date.valueOf(from)); // payment from
                ps.setDate(4, Date.valueOf(to));   // payment to
                ps.setDate(5, Date.valueOf(from)); // fee eligible_from
                ps.setDate(6, Date.valueOf(to));   // fee eligible_from
                ps.setDate(7, Date.valueOf(from)); // fee eligible_from (2ème sous-requête)
                ps.setDate(8, Date.valueOf(to));   // fee eligible_from (2ème sous-requête)

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

                    result.add(stat);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return result;

    }
}

