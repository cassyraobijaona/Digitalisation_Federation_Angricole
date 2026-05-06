package mg.hei.federation_agricole.repository;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class RoleAssignmentRepository {

    public void assign(Connection conn, String collectivityId, String memberId, String role) throws SQLException {

        String sql = """
            INSERT INTO role_assignment(collectivity_id, member_id, role, year)
            VALUES (?, ?, ?::occupation_enum, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collectivityId);
            stmt.setString(2, memberId);
            stmt.setString(3, role);
            stmt.setInt(4, java.time.LocalDate.now().getYear());

            stmt.executeUpdate();
        }
    }
}
