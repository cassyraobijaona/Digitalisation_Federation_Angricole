package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.model.enums.Gender;
import mg.hei.federation_agricole.model.enums.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MemberRepository {

    private final DatabaseConnection db;

    public MemberRepository(DatabaseConnection db) {
        this.db = db;
    }

    public Member findById(String id) throws SQLException {
        String sql = """
            SELECT m.id,
                   p.last_name, p.first_name, p.birth_date,
                   p.gender, p.address, p.profession,
                   p.phone_number, p.email,
                   r.name as occupation
            FROM member m
            JOIN membership ms ON m.membership_id = ms.id
            JOIN membership_payment mp ON ms.payment_id = mp.id
            JOIN person p ON mp.person_id = p.id
            LEFT JOIN position_assignment pa ON pa.member_id = m.id
            LEFT JOIN role r ON r.id = pa.role_id
            WHERE m.id = ?
        """;
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapToMember(rs);
            }
        }
        return null;
    }

    public List<Member> findByCollectivity(int collectivityId) throws SQLException {
        String sql = """
            SELECT m.id,
                   p.last_name, p.first_name, p.birth_date,
                   p.gender, p.address, p.profession,
                   p.phone_number, p.email,
                   r.name as occupation
            FROM member m
            JOIN membership ms ON m.membership_id = ms.id
            JOIN membership_payment mp ON ms.payment_id = mp.id
            JOIN person p ON mp.person_id = p.id
            LEFT JOIN position_assignment pa ON pa.member_id = m.id
            LEFT JOIN role r ON r.id = pa.role_id
            WHERE m.collectivity_id = ?
        """;
        List<Member> members = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(mapToMember(rs));
            }
        }
        return members;
    }

    public Member save(Member member, String collectivityId,
                       List<String> refereeIds) throws SQLException {

        Connection conn = db.getConnection();
        conn.setAutoCommit(false);

        try {
            // 1. Insert person
            int personId;
            String sqlPerson = """
                INSERT INTO person
                (last_name, first_name, birth_date, gender,
                 address, profession, phone_number, email)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
            """;
            try (PreparedStatement ps = conn.prepareStatement(sqlPerson)) {
                ps.setString(1, member.getLastName());
                ps.setString(2, member.getFirstName());
                ps.setDate(3, Date.valueOf(member.getBirthDate()));
                ps.setString(4, member.getGender().name());
                ps.setString(5, member.getAddress());
                ps.setString(6, member.getProfession());
                ps.setString(7, member.getPhoneNumber());
                ps.setString(8, member.getEmail());
                ResultSet rs = ps.executeQuery();
                rs.next();
                personId = rs.getInt("id");
            }

            // 2. Insert membership_payment
            int paymentId;
            String sqlPayment = """
                INSERT INTO membership_payment
                (amount, person_id, receiving_account_id,
                 payment_mode, transaction_reference, status)
                VALUES (50000, ?, 1, 'mobile_money', ?, 'validated')
                RETURNING id
            """;
            try (PreparedStatement ps = conn.prepareStatement(sqlPayment)) {
                ps.setInt(1, personId);
                ps.setString(2, "REF-" + System.currentTimeMillis());
                ResultSet rs = ps.executeQuery();
                rs.next();
                paymentId = rs.getInt("id");
            }

            // 3. Insert membership
            int membershipId;
            String sqlMembership = """
                INSERT INTO membership
                (collectivity_id, payment_id, status)
                VALUES (?, ?, 'validated')
                RETURNING id
            """;
            try (PreparedStatement ps = conn.prepareStatement(sqlMembership)) {
                ps.setInt(1, Integer.parseInt(collectivityId));
                ps.setInt(2, paymentId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                membershipId = rs.getInt("id");
            }

            // 4. Insert member
            int memberId;
            String sqlMember = """
                INSERT INTO member (collectivity_id, membership_id)
                VALUES (?, ?)
                RETURNING id
            """;
            try (PreparedStatement ps = conn.prepareStatement(sqlMember)) {
                ps.setInt(1, Integer.parseInt(collectivityId));
                ps.setInt(2, membershipId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                memberId = rs.getInt("id");
            }

            // 5. Insert sponsorships
            String sqlSponsorship = """
                INSERT INTO sponsorship
                (membership_id, sponsor_id, relationship)
                VALUES (?, ?, 'not_specified')
            """;
            for (String refereeId : refereeIds) {
                try (PreparedStatement ps = conn.prepareStatement(sqlSponsorship)) {
                    ps.setInt(1, membershipId);
                    ps.setInt(2, Integer.parseInt(refereeId));
                    ps.executeUpdate();
                }
            }

            conn.commit();
            member.setId(String.valueOf(memberId));
            return member;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public Member mapToMember(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(String.valueOf(rs.getInt("id")));
        m.setLastName(rs.getString("last_name"));
        m.setFirstName(rs.getString("first_name"));
        m.setBirthDate(rs.getDate("birth_date").toLocalDate());
        m.setGender(Gender.valueOf(rs.getString("gender").toUpperCase()));
        m.setAddress(rs.getString("address"));
        m.setProfession(rs.getString("profession"));
        m.setPhoneNumber(rs.getString("phone_number"));
        m.setEmail(rs.getString("email"));
        String occ = rs.getString("occupation");
        if (occ != null) {
            m.setOccupation(MemberOccupation.valueOf(occ.toUpperCase()));
        }
        return m;
    }
}