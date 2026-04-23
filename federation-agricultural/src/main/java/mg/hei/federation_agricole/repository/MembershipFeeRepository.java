package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.MembershipFee;
import mg.hei.federation_agricole.model.enums.ActivityStatus;
import mg.hei.federation_agricole.model.enums.Frequency;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MembershipFeeRepository {
    private final DatabaseConnection databaseConnection;

    public MembershipFeeRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection =databaseConnection;
    }

    public List<MembershipFee> findByCollectivity(Integer collectivityId) {
        List<MembershipFee> list = new ArrayList<>();

        String sql = "SELECT id, eligible_from , frequency, amount, label,status status FROM membership_fee WHERE collectivity_id=?";

        try (Connection conn = databaseConnection.getConnection();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, collectivityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MembershipFee f = new MembershipFee();
                f.setId(rs.getInt("id"));
                f.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
                f.setFrequency(Frequency.valueOf(rs.getString("frequency")));
                f.setAmount(rs.getDouble("amount"));
                f.setLabel(rs.getString("label"));
                f.setStatus(ActivityStatus.valueOf(rs.getString("status")));

                list.add(f);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    public List<MembershipFee> saveAll(Integer collectivityId, List<MembershipFee> fees) {
        String sql = """
            INSERT INTO membership_fee(
                collectivity_id,
                eligible_from,
                frequency,
                amount,
                label,
                status
            )
            VALUES (?, ?, ?::frequency_enum, ?, ?, 'ACTIVE')
            RETURNING id
        """;

        List<MembershipFee> result = new ArrayList<>();

        try (Connection con = databaseConnection.getConnection()) {

            for (MembershipFee f : fees) {

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, collectivityId);
                ps.setDate(2, Date.valueOf(f.getEligibleFrom()));
                ps.setString(3, f.getFrequency().name());
                ps.setDouble(4, f.getAmount());
                ps.setString(5, f.getLabel());

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    f.setId(rs.getInt("id"));
                    f.setStatus(ActivityStatus.ACTIVE);
                }

                result.add(f);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

