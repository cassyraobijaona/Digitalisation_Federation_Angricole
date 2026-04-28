package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.MemberPayment;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Repository
public class MemberPaymentRepository {

    private final DatabaseConnection databaseConnection;
    private final TransactionRepository transactionRepository;

    public MemberPaymentRepository( DatabaseConnection databaseConnection,TransactionRepository tr) {
        this.databaseConnection = databaseConnection;
        this.transactionRepository = tr;
    }
    public void save(MemberPayment p) {

        String sql = """
        INSERT INTO member_payment(
            id,
            member_id,
            membership_fee_id,
            account_id,
            amount,
            payment_mode,
            creation_date
        )
        VALUES (?,?, ?, ?, ?, ?::payment_mode_enum, CURRENT_DATE)
    """;

        try (Connection con = databaseConnection.getConnection()) {

            if (p.getMembershipFeeIdentifier() == null) {
                throw new IllegalArgumentException("membershipFeeIdentifier is required");
            }

            if (p.getAccountCreditedIdentifier() == null) {
                throw new IllegalArgumentException("accountCreditedIdentifier is required");
            }

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, p.getId());
            ps.setString(2, p.getMemberId());
            ps.setString(3, p.getMembershipFeeIdentifier());
            ps.setString(4, p.getAccountCreditedIdentifier());
            ps.setDouble(5, p.getAmount());
            ps.setString(6, p.getPaymentMode().name());

            ps.executeUpdate();

            transactionRepository.createFromPayment(con, p);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
