package mg.hei.federation_agricole.controller;
import mg.hei.federation_agricole.config.DatabaseConnection;
import mg.hei.federation_agricole.model.dto.CreateMember;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.repository.MemberRepository;
import mg.hei.federation_agricole.repository.RefereeRepository;
import mg.hei.federation_agricole.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final DatabaseConnection db;
    private final MemberRepository memberRepo;
    private final RefereeRepository refereeRepo;
    private final MemberService service;

    public MemberController(DatabaseConnection db,
                            MemberRepository memberRepo,
                            RefereeRepository refereeRepo,
                            MemberService service) {
        this.db = db;
        this.memberRepo = memberRepo;
        this.refereeRepo = refereeRepo;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody java.util.List<CreateMember> members) {

        java.util.List<String> ids = new java.util.ArrayList<>();

        for (CreateMember m : members) {

            try (Connection conn = db.getConnection()) {

                conn.setAutoCommit(false);

                service.validate(m, conn);

                String id = memberRepo.save( m);
                refereeRepo.save(conn, id, m.getReferees());


                conn.commit();

                ids.add(id);

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return ResponseEntity.status(201).body(ids);
    }
}