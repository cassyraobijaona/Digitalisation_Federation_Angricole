package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.model.dto.MemberPayment;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Repository
public class MemberPaymentRepository {

    private final DataSource dataSource;
    private final TransactionRepository transactionRepository;

    public MemberPaymentRepository(DataSource ds, TransactionRepository tr) {
        this.dataSource = ds;
        this.transactionRepository = tr;
    }

    public void save(MemberPayment p) {

        String sql = """
            INSERT INTO member_payment(member_id, membership_fee_id, account_id, amount, payment_mode, creation_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_DATE)
        """;

        try (Connection con = dataSource.getConnection()) {

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, p.getMemberId());
            ps.setInt(2, p.getMembershipFeeId());
            ps.setInt(3, p.getAccountId());
            ps.setDouble(4, p.getAmount());
            ps.setString(5, p.getPaymentMode().name());

            ps.executeUpdate();

            transactionRepository.createFromPayment(con, p);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
