package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class CollectivityRepository {

    private final DatabaseConnection db;

    public CollectivityRepository(DatabaseConnection db) {
        this.db = db;
    }

    public int save(String location) throws SQLException {
        String sql = """
            INSERT INTO collectivity
            (number, name, agricultural_specialty, creation_date, city_id, federation_id)
            VALUES (?, ?, 'Not defined', CURRENT_DATE, 1, 1)
            RETURNING id
        """;
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String number = "COLL-" + System.currentTimeMillis();
            ps.setString(1, number);
            ps.setString(2, location);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("id");
        }
    }
}