package mg.hei.federation_agricole.controller;
import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.exception.BadRequestException;
import mg.hei.federation_agricole.model.dto.CreateMember;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.repository.CollectivityMemberRepository;
import mg.hei.federation_agricole.repository.MemberRepository;
import mg.hei.federation_agricole.repository.RefereeRepository;
import mg.hei.federation_agricole.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final DatabaseConnection db;
    private final MemberRepository memberRepo;
    private final RefereeRepository refereeRepo;
    private final MemberService service;
    private final CollectivityMemberRepository cmRepo;

    public MemberController(DatabaseConnection db,
                            MemberRepository memberRepo,
                            RefereeRepository refereeRepo,
                            MemberService service,
                            CollectivityMemberRepository cmRepo) {
        this.db = db;
        this.memberRepo = memberRepo;
        this.refereeRepo = refereeRepo;
        this.service = service;
        this.cmRepo = cmRepo;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody List<CreateMember> members) throws SQLException {

        if (members == null || members.isEmpty())
            throw new BadRequestException("Members list cannot be empty");

        List<String> ids = new ArrayList<>();

        for (CreateMember m : members) {

            Connection conn = null;

            try {

                conn = db.getConnection();
                conn.setAutoCommit(false);

                service.validate(m, conn);

                String id = memberRepo.save(conn, m);

                cmRepo.save(conn, m.getCollectivityIdentifier(), id);

                refereeRepo.save(conn, id, m.getReferees(), m.getRelation());

                conn.commit();

                ids.add(id);

            } catch (Exception e) {

                if (conn != null) {
                    conn.rollback();
                }

                throw new BadRequestException(e.getMessage());

            } finally {

                if (conn != null) {
                    conn.close();
                }
            }
        }

        return ResponseEntity.status(201).body(ids); // 201
    }
}