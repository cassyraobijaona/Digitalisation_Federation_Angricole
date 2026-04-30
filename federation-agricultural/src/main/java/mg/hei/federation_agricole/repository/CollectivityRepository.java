package mg.hei.federation_agricole.repository;
import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.Collectivity;
import mg.hei.federation_agricole.model.dto.CreateCollectivity;
import mg.hei.federation_agricole.model.dto.FinancialAccountResponse;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CollectivityRepository {
    private final DatabaseConnection databaseConnection;
    public CollectivityRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    public CreateCollectivity save(String id, String location, String specialization, boolean approval) throws SQLException {
        CreateCollectivity c = new CreateCollectivity();
        String sql = """
            INSERT INTO collectivity(id,location,specialization, federation_approval)
            VALUES (?,?, ?,?)
            RETURNING id, location,specialization, federation_approval
        """;


        try (Connection conn = databaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,id);
            stmt.setString(2, location);
            stmt.setString(3, specialization);
            stmt.setBoolean(4, approval);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                c.setId(rs.getString("id"));
                c.setLocation(rs.getString("location"));
                c.setSpecialization(rs.getString("specialization"));
                c.setFederationApproval(rs.getBoolean("federation_approval"));

            }

        }catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return c;
    }
    public Collectivity findById(String id) throws SQLException {

        String sql = "SELECT id,name,location,number, federation_approval FROM collectivity WHERE id=?";

        try (Connection conn = databaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            Collectivity c = new Collectivity();
            c.setId(rs.getString("id"));
            c.setName(rs.getString("name")); // 🔥 MISSING
            c.setNumber(rs.getObject("number", Integer.class)); // 🔥 MISSING
            c.setLocation(rs.getString("location"));
            c.setFederationApproval(rs.getBoolean("federation_approval"));

            return c;
        }
    }
   /* public void updateInfo(Connection conn, Integer id, String location, boolean approval) throws SQLException {

        String sql = """
        UPDATE collectivity
        SET location=?, federation_approval=?
        WHERE id=?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location);
            stmt.setBoolean(2, approval);
            stmt.setInt(3, id);

            stmt.executeUpdate();
        }
    }*/
   public void updateCollectivity( Collectivity c) throws SQLException {

       String sql = "UPDATE collectivity SET name=?, number=?, location=? WHERE id=?";

       try (Connection conn = databaseConnection.getConnection()) {
           PreparedStatement ps = conn.prepareStatement(sql);
           ps.setString(1, c.getName());
           ps.setObject(2, c.getNumber());
           ps.setString(3, c.getLocation());
           ps.setString(4, c.getId());

           int rows = ps.executeUpdate();
           System.out.println("ROWS UPDATED = " + rows);

           if (rows == 0) {
               throw new RuntimeException("UPDATE FAILED: ID NOT FOUND");
           }
       }
   }
    public List<FinancialAccountResponse> findFinancialAccountsByCollectivityAndDate(
            String collectivityId,
            LocalDate at
    ) {

        String sql = """
        SELECT 
            fa.id,
            fa.account_type,
            fa.amount + COALESCE(SUM(ct.amount), 0) AS balance
        FROM financial_account fa
        LEFT JOIN collectivity_transaction ct 
            ON ct.account_id = fa.id
            AND ct.creation_date <= ?
        WHERE fa.owner_id = ?
        GROUP BY fa.id, fa.account_type, fa.amount
    """;

        List<FinancialAccountResponse> result = new ArrayList<>();

        try (Connection con = databaseConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(at));
            ps.setString(2, collectivityId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                FinancialAccountResponse f = new FinancialAccountResponse(
                        rs.getString("id"),
                        rs.getString("account_type"),
                        rs.getDouble("balance")
                );

                result.add(f);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}