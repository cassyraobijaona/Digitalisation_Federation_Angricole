package mg.hei.federation_agricole.repository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CollectivityRepository {

    public int save(Connection conn, String location, boolean approval) throws SQLException {

        String sql = """
            INSERT INTO collectivity(location, federation_approval)
            VALUES (?, ?)
            RETURNING id
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location);
            stmt.setBoolean(2, approval);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }
}