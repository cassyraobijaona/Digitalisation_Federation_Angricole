package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.model.dto.MembershipFee;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MembershipFeeRepository {

    private final DataSource dataSource;

    public MembershipFeeRepository(DataSource ds) {
        this.dataSource = ds;
    }

    public List<MembershipFee> findByCollectivity(int collectivityId) {
        List<MembershipFee> list = new ArrayList<>();

        String sql = "SELECT * FROM membership_fee WHERE collectivity_id=?";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, collectivityId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MembershipFee f = new MembershipFee();
                f.setId(rs.getInt("id"));
                f.setAmount(rs.getDouble("amount"));
                list.add(f);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
