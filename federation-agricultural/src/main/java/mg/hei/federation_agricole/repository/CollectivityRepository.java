package mg.hei.federation_agricole.repository;
import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.Collectivity;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CollectivityRepository {
    private final DatabaseConnection databaseConnection;
    public CollectivityRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    public Integer save( String location, boolean approval) throws SQLException {

        String sql = """
            INSERT INTO collectivity(location, federation_approval)
            VALUES (?, ?)
            RETURNING id
        """;

        try (Connection conn = databaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, location);
            stmt.setBoolean(2, approval);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }
    public Collectivity findById(Connection conn, Integer id) throws SQLException {

        String sql = "SELECT * FROM collectivity WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            Collectivity c = new Collectivity();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            c.setNumber(rs.getObject("number", Integer.class));

            return c;
        }
    }
    public void updateInfo(Connection conn, Integer id, String location, boolean approval) throws SQLException {

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
    }
    public void updateCollectivity(Connection conn, Collectivity c) throws SQLException {

        String sql = "UPDATE collectivity SET name=?, number=?, location=? WHERE id=?";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, c.getName());
        ps.setObject(2, c.getNumber()); // important Integer safe
        ps.setString(3, c.getLocation());
        ps.setInt(4, c.getId());

        ps.executeUpdate(); // 🔥 OBLIGATOIRE
    }
}