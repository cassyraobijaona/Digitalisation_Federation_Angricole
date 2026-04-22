package  mg.hei.federation_agricole.service;
import mg.hei.federation_agricole.model.dto.CreateCollectivity;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class CollectivityService {

    private final MemberRepository memberRepo;

    public CollectivityService(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    public void validate(CreateCollectivity c, Connection conn) throws Exception {

        if (!c.isFederationApproval())
            throw new RuntimeException("No federation approval");

        if (c.getMembers().size() < 10)
            throw new RuntimeException("Need 10 members");

        int old = 0;

        for (String id : c.getMembers()) {

            Member m = memberRepo.findById(conn, Integer.parseInt(id));

            if (m.getAdhesionDate()
                    .isBefore(java.time.LocalDate.now().minusMonths(6))) {
                old++;
            }
        }

        if (old < 5)
            throw new RuntimeException("Need 5 senior members");
    }
}