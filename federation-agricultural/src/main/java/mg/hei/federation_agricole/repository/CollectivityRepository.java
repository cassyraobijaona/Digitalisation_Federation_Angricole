package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.Collectivity;
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

    public Collectivity findById(int id) throws SQLException {
        String sql = """
        SELECT id, number, name, agricultural_specialty,
               creation_date, city_id, federation_id
        FROM collectivity
        WHERE id = ?
    """;
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Collectivity c = new Collectivity();
                c.setId(String.valueOf(rs.getInt("id")));
                c.setLocation(rs.getString("city_id"));
                c.setNumber(rs.getString("number"));
                c.setName(rs.getString("name"));
                return c;
            }
        }
        return null;
    }

    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM collectivity WHERE name = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public Collectivity assignIdentity(int id, String number,
                                       String name) throws SQLException {
        String sql = """
        UPDATE collectivity
        SET number = ?, name = ?
        WHERE id = ?
        RETURNING id, number, name
    """;
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            ps.setString(2, name);
            ps.setInt(3, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Collectivity c = new Collectivity();
                c.setId(String.valueOf(rs.getInt("id")));
                c.setNumber(rs.getString("number"));
                c.setName(rs.getString("name"));
                return c;
            }
        }
        return null;
    }
}