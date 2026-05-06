package mg.hei.federation_agricole.repository;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class CollectivityMemberRepository {

    public void save(Connection conn, String collectivityId, String memberId)  {
        String sql = """
            INSERT INTO collectivity_member (collectivity_id, member_id)
            VALUES (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setString(2, memberId);
            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
