package mg.hei.federation_agricole.repository;
import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.CreateMember;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.model.enums.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class MemberRepository {
    private final DatabaseConnection databaseConnection;
    public MemberRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    public Integer save( mg.hei.federation_agricole.model.dto.CreateMember m) throws SQLException {

        String sql = """
            INSERT INTO member(
                first_name, last_name, birth_date, gender,
                address, profession, phone_number, email, occupation
            ) VALUES (?, ?, ?, ?::gender_enum, ?, ?, ?, ?, ?::occupation_enum)
            RETURNING id
        """;

        try (Connection conn = databaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, m.getFirstName());
            stmt.setString(2, m.getLastName());
            stmt.setDate(3, Date.valueOf(m.getBirthDate()));
            stmt.setString(4, m.getGender().name());
            stmt.setString(5, m.getAddress());
            stmt.setString(6, m.getProfession());
            stmt.setString(7, m.getPhoneNumber());
            stmt.setString(8, m.getEmail());
            stmt.setString(9, m.getOccupation().name());

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("id");
        }
    }

    public Member findById(Connection conn, int id) throws SQLException {

        String sql = "SELECT id,occupation,adhesion_date FROM member WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            Member m = new Member();
            m.setId(id);
            m.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));
            m.setAdhesionDate(rs.getDate("adhesion_date").toLocalDate());

            return m;
        }
    }
}