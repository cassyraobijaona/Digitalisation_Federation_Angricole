package mg.hei.federation_agricole.repository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class RefereeRepository {

    public void save(Connection conn, String memberId, java.util.List<String> refs,String relation) throws SQLException {

        String sql = "INSERT INTO referee(member_id, referee_id,relation) VALUES (?, ?,?)";

        for (String r : refs) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, memberId);
                stmt.setString(2, r);
                stmt.setString(3, relation);
                stmt.executeUpdate();
            }
        }
    }
}