package mg.hei.federation_agricole.repository;
import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.CreateMember;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.model.enums.Gender;
import mg.hei.federation_agricole.model.enums.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MemberRepository {
    private final DatabaseConnection databaseConnection;
    public MemberRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    public String save( CreateMember m) throws SQLException {

        String sql = """
            INSERT INTO member(
                id,               
                first_name, last_name, birth_date, gender,
                address, profession, phone_number, email, occupation
            ) VALUES (?,?, ?, ?, ?::gender_enum, ?, ?, ?, ?, ?::occupation_enum)
            RETURNING id
        """;

        try (Connection conn = databaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, m.getId());
            stmt.setString(2, m.getFirstName());
            stmt.setString(3, m.getLastName());
            stmt.setDate(4, Date.valueOf(m.getBirthDate()));
            stmt.setString(5, m.getGender().name());
            stmt.setString(6, m.getAddress());
            stmt.setString(7, m.getProfession());
            stmt.setString(8, m.getPhoneNumber());
            stmt.setString(9, m.getEmail());
            stmt.setString(10, m.getOccupation().name());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("id");
        }catch (Exception e) {
            throw new SQLException(e);
        }
        return null;
    }

    public Member findById(Connection conn, String id) throws SQLException {

        String sql = "SELECT id,occupation,adhesion_date FROM member WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            Member m = new Member();
            m.setId(id);
            m.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));
            m.setAdhesionDate(rs.getDate("adhesion_date").toLocalDate());

            return m;
        }
    }
    public List<Member> findByCollectivityId(String collectivityId) {

        String sql = """
        SELECT m.*
        FROM member m
        JOIN collectivity_member cm ON cm.member_id = m.id
        WHERE cm.collectivity_id = ?
    """;

        List<Member> result = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, collectivityId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Member m = new Member();
                m.setId(rs.getString("id"));
                m.setFirstName(rs.getString("first_name"));
                m.setLastName(rs.getString("last_name"));
                m.setBirthDate(rs.getDate("birth_date").toLocalDate());
                m.setGender(Gender.valueOf(rs.getString("gender"))); // adapte enum si besoin
                m.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));

                result.add(m);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}