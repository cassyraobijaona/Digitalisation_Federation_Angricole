package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.FinancialAccount;
import mg.hei.federation_agricole.model.dto.FinancialAccountEntity;
import mg.hei.federation_agricole.repository.mapper.FinancialAccountFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FinancialAccountRepository {

    private final DatabaseConnection databaseConnection;

    public FinancialAccountRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public FinancialAccount save(FinancialAccountEntity account) {

        String sql = """
            INSERT INTO financial_account(id,owner_type, collectivity_id, account_type, amount)
            VALUES (?,?::owner_type_enum, ?, ?::account_type_enum, ?)
            RETURNING id
        """;

        try (Connection con = databaseConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, account.getId());
            ps.setString(2, account.getOwnerType().name());
            ps.setString(3, account.getCollectivity_id());
            ps.setString(4, account.getAccountType().name());
            ps.setDouble(5, account.getAmount());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                account.setId(rs.getString("id"));
            }

            return account;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public FinancialAccount findById(String id) {

        String sql = """
    SELECT 
        fa.id               AS fa_id,
        fa.owner_type,
        fa.collectivity_id,
        fa.account_type,
        fa.amount           AS fa_amount,
        b.holder_name       AS bank_holder,
        b.bank_name,
        b.bank_code,
        b.bank_branch_code,
        b.bank_account_number,
        b.bank_account_key,
        m.holder_name       AS mobile_holder,
        m.mobile_service,
        m.mobile_number
    FROM financial_account fa
    LEFT JOIN bank_account b   ON b.id = fa.id
    LEFT JOIN mobile_account m ON m.id = fa.id
    WHERE fa.collectivity_id = ?
""";
        try (Connection con = databaseConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return FinancialAccountFactory.map(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
