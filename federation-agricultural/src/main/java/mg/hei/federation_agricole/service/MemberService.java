package mg.hei.federation_agricole.service;

import mg.hei.federation_agricole.model.dto.CreateMember;
import mg.hei.federation_agricole.model.dto.Member;
import mg.hei.federation_agricole.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> createMembers(List<CreateMember> requests) {
        List<Member> result = new ArrayList<>();

        for (CreateMember req : requests) {

            // Collectivity check
            if (req.getCollectivityIdentifier() == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Collectivity not found");
            }

            // Minimum 2 referees
            if (req.getReferees() == null
                    || req.getReferees().size() < 2) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "At least 2 referees required");
            }

            // Registration fee check
            if (req.getRegistrationFeePaid() == null
                    || !req.getRegistrationFeePaid()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Registration fee not paid");
            }

            // Membership dues check
            if (req.getMembershipDuesPaid() == null
                    || !req.getMembershipDuesPaid()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Membership dues not paid");
            }

            // Check referees exist
            List<Member> refereeMembers = new ArrayList<>();
            for (String refereeId : req.getReferees()) {
                try {
                    Member referee = memberRepository.findById(refereeId);
                    if (referee == null) {
                        throw new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Member not found: " + refereeId);
                    }
                    refereeMembers.add(referee);
                } catch (SQLException e) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            e.getMessage());
                }
            }

            // Save member
            try {
                Member member = new Member();
                member.setFirstName(req.getFirstName());
                member.setLastName(req.getLastName());
                member.setBirthDate(req.getBirthDate());
                member.setGender(req.getGender());
                member.setAddress(req.getAddress());
                member.setProfession(req.getProfession());
                member.setPhoneNumber(req.getPhoneNumber());
                member.setEmail(req.getEmail());
                member.setOccupation(req.getOccupation());

                Member saved = memberRepository.save(
                        member,
                        req.getCollectivityIdentifier(),
                        req.getReferees()
                );
                saved.setReferees(refereeMembers);
                result.add(saved);

            } catch (SQLException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        e.getMessage());
            }
        }
        return result;
    }
}