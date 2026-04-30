package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.model.enums.PaymentMode;
import mg.hei.federation_agricole.repository.mapper.FinancialAccountFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {
    private final DatabaseConnection databaseConnection;

    public TransactionRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    @Override
    public void createFromPayment(Connection con, MemberPayment p) {

        String sql = """
            INSERT INTO collectivity_transaction(collectivity_id, member_id, account_id, amount, payment_mode, creation_date)
            VALUES (?, ?, ?, ?, ?::payment_mode_enum, CURRENT_DATE)
        """;

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, p.getMemberId());
            ps.setString(2, p.getMemberId());
            ps.setString(3, p.getAccountCreditedIdentifier());
            ps.setDouble(4, p.getAmount());
            ps.setString(5, p.getPaymentMode().name());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<CollectivityTransaction> findByPeriod(String id, LocalDate from, LocalDate to) {

        String sql = """
        SELECT
                                             ct.id,
                                             ct.collectivity_id,
                                             ct.member_id,
                                             ct.account_id,
                                             ct.amount,
                                             ct.payment_mode,
                                             ct.creation_date,
                                         
                                             m.first_name,
                                             m.last_name,
                                         
                                             fa.id as fa_id,
                                             fa.collectivity_id,
                                             fa.owner_type,
                                             fa.amount as fa_amount,
                                         
                                             ba.holder_name as bank_holder,
                                             ba.bank_name,
                                             ba.bank_code,
                                             ba.bank_branch_code,
                                             ba.bank_account_number,
                                             ba.bank_account_key,
                                         
                                             ma.holder_name as mobile_holder,
                                             ma.mobile_service,
                                             ma.mobile_number
                                         
                                         FROM collectivity_transaction ct
                                         JOIN member m ON ct.member_id = m.id
                                         JOIN financial_account fa ON ct.account_id = fa.id
                                         LEFT JOIN bank_account ba ON fa.id = ba.id
                                         LEFT JOIN mobile_account ma ON fa.id = ma.id
                                         
                                         WHERE ct.collectivity_id = ?
                                         AND ct.creation_date BETWEEN ? AND ?
    """;

        List<CollectivityTransaction> list = new ArrayList<>();

        try (Connection con = databaseConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, id);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                CollectivityTransaction t = new CollectivityTransaction();

                t.setId(rs.getString("id"));
                t.setAmount(rs.getDouble("amount"));
                t.setCreationDate(rs.getDate("creation_date").toLocalDate());
                t.setPaymentMode(
                        PaymentMode.valueOf(rs.getString("payment_mode"))
                );

                // 👤 MEMBER
                Member m = new Member();
                m.setId(rs.getString("member_id"));
                m.setFirstName(rs.getString("first_name"));
                m.setLastName(rs.getString("last_name"));

                t.setMemberDebited(m);

                // 💰 ACCOUNT (ONEOF)
                FinancialAccount acc = FinancialAccountFactory.map(rs);
                t.setAccountCredited(acc);

                list.add(t);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
