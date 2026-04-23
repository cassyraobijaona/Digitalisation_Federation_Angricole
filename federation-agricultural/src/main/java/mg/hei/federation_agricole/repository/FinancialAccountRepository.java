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
            INSERT INTO financial_account(owner_type, owner_id, account_type, amount)
            VALUES (?, ?, ?, ?)
            RETURNING id
        """;

        try (Connection con = databaseConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, account.getOwnerType().name());
            ps.setInt(2, account.getOwnerId());
            ps.setString(3, account.getAccountType().name());
            ps.setDouble(4, account.getAmount());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                account.setId(rs.getInt("id"));
            }

            return account;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public FinancialAccount findById(Integer id) {

        String sql = "SELECT owner_type, owner_id, account_type, amount FROM financial_account WHERE id = ?";

        try (Connection con = databaseConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

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
