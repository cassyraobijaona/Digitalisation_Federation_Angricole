package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.model.dto.CollectivityTransaction;
import mg.hei.federation_agricole.model.dto.MemberPayment;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    @Override
    public void createFromPayment(Connection con, MemberPayment p) {

        String sql = """
            INSERT INTO collectivity_transaction(collectivity_id, member_id, account_id, amount, payment_mode, creation_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_DATE)
        """;

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, 1); // collectivity simplifié
            ps.setInt(2, p.getMemberId());
            ps.setInt(3, p.getAccountId());
            ps.setDouble(4, p.getAmount());
            ps.setString(5, p.getPaymentMode().name());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CollectivityTransaction> findByPeriod(int id, LocalDate from, LocalDate to) {
        return new ArrayList<>();
    }
}
