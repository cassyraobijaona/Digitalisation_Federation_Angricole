package mg.hei.federation_agricole.service;
import mg.hei.federation_agricole.exception.BadRequestException;
import mg.hei.federation_agricole.model.dto.CreateMember;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.time.LocalDate;

@Service
public class MemberService {

    private final MemberRepository memberRepo;

    public MemberService(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    public void validate(CreateMember m, Connection conn) throws Exception {

        if (m.getReferees() == null || m.getReferees().size() < 2)
            throw new BadRequestException("At least 2 referees required");

        for (String id : m.getReferees()) {
            Member ref = memberRepo.findById(conn, id);
            if (ref == null)
                throw new BadRequestException("Referee not found: " + id);
            if (!ref.getOccupation().name().equals("SENIOR"))
                throw new BadRequestException("Referee must be SENIOR: " + id);
            if (ref.getAdhesionDate().isAfter(LocalDate.now().minusDays(90)))
                throw new BadRequestException("Referee seniority must exceed 90 days: " + id);
        }

        if (!m.isRegistrationFeePaid())
            throw new BadRequestException("Registration fee must be paid");

        if (!m.isMembershipDuesPaid())
            throw new BadRequestException("Membership dues must be paid");
    }
}