package mg.hei.federation_agricole.service;
import mg.hei.federation_agricole.model.dto.CreateMember;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class MemberService {

    private final MemberRepository memberRepo;

    public MemberService(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    public void validate(CreateMember m, Connection conn) throws Exception {

        if (m.getReferees() == null || m.getReferees().size() < 2)

            throw new RuntimeException("At least 2 referees required");

        for (String id : m.getReferees()) {

            Member ref = memberRepo.findById(conn, id);

            if (ref == null)
                throw new RuntimeException("Referee not found");

            if (!ref.getOccupation().name().equals("SENIOR"))
                throw new RuntimeException("Referee must be SENIOR");
        }

        if (!m.isRegistrationFeePaid() || !m.isMembershipDuesPaid())
            throw new RuntimeException("Payment required");
    }
}