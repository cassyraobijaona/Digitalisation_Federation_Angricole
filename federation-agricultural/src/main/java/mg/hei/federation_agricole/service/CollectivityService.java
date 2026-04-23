package  mg.hei.federation_agricole.service;
import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.repository.CollectivityRepository;
import mg.hei.federation_agricole.repository.MemberPaymentRepository;
import mg.hei.federation_agricole.repository.MemberRepository;
import mg.hei.federation_agricole.repository.MembershipFeeRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;

@Service
public class CollectivityService {

    private final MemberRepository memberRepo;
    private final CollectivityRepository collecRepo;
    private final MembershipFeeRepository feeRepo;
    private final MemberPaymentRepository paymentRepo;
    public CollectivityService(MembershipFeeRepository feeRepo, MemberPaymentRepository paymentRepo,MemberRepository memberRepo, CollectivityRepository collecRepo) {
        this.memberRepo = memberRepo;
        this.collecRepo = collecRepo;
        this.feeRepo = feeRepo;
        this.paymentRepo = paymentRepo;
    }


    public void validate(CreateCollectivity c, Connection conn) throws Exception {

        if (!c.isFederationApproval())
            throw new RuntimeException("No federation approval");

        if (c.getMembers().size() < 10)
            throw new RuntimeException("Need 10 members");

        Integer old = 0;

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
    public void validateUpdate(Collectivity existing, CollectivityInformation input) {

        System.out.println("EXISTING NAME = " + existing.getName());
        System.out.println("INPUT NAME = " + input.getName());
        System.out.println("EXISTING NUMBER = " + existing.getNumber());
        System.out.println("INPUT NUMBER = " + input.getNumber());

        // 🔥 NUMBER
        if (input.getNumber() != null) {

            if (existing.getNumber() != null &&
                    !input.getNumber().equals(existing.getNumber())) {
                throw new RuntimeException("NUMBER CANNOT BE MODIFIED");
            }
        }

        // 🔥 NAME
        if (input.getName() != null) {

            if (existing.getName() != null &&
                    !input.getName().equals(existing.getName())) {
                throw new RuntimeException("NAME CANNOT BE MODIFIED");
            }
        }
    }
    public Collectivity update(Collectivity existing, CollectivityInformation input, Connection conn) throws Exception {

        validateUpdate(existing, input);

        collecRepo.updateCollectivity( existing);

        conn.commit(); // 🔥 OBLIGATOIRE si autoCommit=false

        return existing;
    }

    public List<MembershipFee> getFees(Integer collectivityId) {
        return feeRepo.findByCollectivity(collectivityId);
    }

    public List<MembershipFee> create(Integer collectivityId, List<MembershipFee> fees) {

        for (MembershipFee f : fees) {

            if (f.getAmount() <= 0) {
                throw new RuntimeException("Amount must be > 0");
            }

            if (f.getFrequency() == null) {
                throw new RuntimeException("Frequency required");
            }
        }

        return feeRepo.saveAll(collectivityId, fees);
    }


    public void pay(MemberPayment payment) {
        paymentRepo.save(payment);
    }



}