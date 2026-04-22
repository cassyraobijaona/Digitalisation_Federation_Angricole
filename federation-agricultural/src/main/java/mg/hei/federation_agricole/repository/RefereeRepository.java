package mg.hei.federation_agricole.repository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class RefereeRepository {

    public void save(Connection conn, int memberId, java.util.List<String> refs) throws SQLException {

        String sql = "INSERT INTO referee(member_id, referee_id) VALUES (?, ?)";

        for (String r : refs) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, memberId);
                stmt.setInt(2, Integer.parseInt(r));
                stmt.executeUpdate();
            }
        }
    }
}