package mg.hei.federation_agricole.repository;


import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.CollectivityInformation;
import mg.hei.federation_agricole.model.dto.CollectivityLocalStatistics;
import mg.hei.federation_agricole.model.dto.CollectivityOverallStatistics;
import mg.hei.federation_agricole.model.dto.MemberDescription;
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

    public List<CollectivityLocalStatistics> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) throws SQLException {

        List<CollectivityLocalStatistics> result = new ArrayList<>();

        // Récupérer tous les membres de la collectivité
        String memberSql = """
            SELECT m.id, m.first_name, m.last_name, m.email, m.occupation
            FROM member m
            JOIN collectivity_member cm ON cm.member_id = m.id
            WHERE cm.collectivity_id = ?
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(memberSql)) {

            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String memberId = rs.getString("id");

                MemberDescription desc = new MemberDescription();
                desc.setId(memberId);
                desc.setFirstName(rs.getString("first_name"));
                desc.setLastName(rs.getString("last_name"));
                desc.setEmail(rs.getString("email"));
                desc.setOccupation(rs.getString("occupation"));

                // Montant encaissé par ce membre sur la période
                double earned = getEarnedAmount(conn, memberId, collectivityId, from, to);

                // Montant impayé potentiel (cotisations actives non payées)
                double unpaid = getUnpaidAmount(conn, memberId, collectivityId, from, to);

                CollectivityLocalStatistics stat = new CollectivityLocalStatistics();
                stat.setMemberDescription(desc);
                stat.setEarnedAmount(earned);
                stat.setUnpaidAmount(unpaid);

                result.add(stat);
            }
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

    private double getUnpaidAmount(Connection conn, String memberId,
                                   String collectivityId, LocalDate from, LocalDate to) throws SQLException {

        // Total dû = somme des cotisations ACTIVES de la collectivité
        String totalDueSql = """
            SELECT COALESCE(SUM(mf.amount), 0) as total
            FROM membership_fee mf
            WHERE mf.collectivity_id = ?
            AND mf.status = 'ACTIVE'
            AND mf.eligible_from <= ?
        """;

        // Total payé par ce membre
        String totalPaidSql = """
            SELECT COALESCE(SUM(mp.amount), 0) as total
            FROM member_payment mp
            JOIN membership_fee mf ON mf.id = mp.membership_fee_id
            WHERE mp.member_id = ?
            AND mf.collectivity_id = ?
            AND mf.status = 'ACTIVE'
            AND mp.creation_date BETWEEN ? AND ?
        """;

        double totalDue = 0;
        double totalPaid = 0;

        try (PreparedStatement ps = conn.prepareStatement(totalDueSql)) {
            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            rs.next();
            totalDue = rs.getDouble("total");
        }

        try (PreparedStatement ps = conn.prepareStatement(totalPaidSql)) {
            ps.setString(1, memberId);
            ps.setString(2, collectivityId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            rs.next();
            totalPaid = rs.getDouble("total");
        }

        double unpaid = totalDue - totalPaid;
        return unpaid < 0 ? 0 : unpaid;
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

