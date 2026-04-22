package mg.hei.federation_agricole.service;

import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.repository.CollectivityRepository;
import mg.hei.federation_agricole.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityService(
            CollectivityRepository collectivityRepository,
            MemberRepository memberRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }

    public List<Collectivity> createCollectivities(
            List<CreateCollectivity> requests) {

        List<Collectivity> result = new ArrayList<>();

        for (CreateCollectivity req : requests) {

            // Federation approval check
            if (req.getFederationApproval() == null
                    || !req.getFederationApproval()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Federation approval required");
            }

            // Structure check
            if (req.getStructure() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Structure is required");
            }

            // Minimum 10 members
            if (req.getMembers() == null
                    || req.getMembers().size() < 10) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "At least 10 members required");
            }

            // Resolve members
            List<Member> members = new ArrayList<>();
            for (String memberId : req.getMembers()) {
                try {
                    Member m = memberRepository.findById(memberId);
                    if (m == null) {
                        throw new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Member not found: " + memberId);
                    }
                    members.add(m);
                } catch (SQLException e) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            e.getMessage());
                }
            }

            // Resolve structure
            CollectivityStructure structure =
                    resolveStructure(req.getStructure());

            // Save collectivity
            try {
                int collectivityId =
                        collectivityRepository.save(req.getLocation());

                Collectivity collectivity = new Collectivity();
                collectivity.setId(String.valueOf(collectivityId));
                collectivity.setLocation(req.getLocation());
                collectivity.setStructure(structure);
                collectivity.setMembers(members);
                result.add(collectivity);

            } catch (SQLException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        e.getMessage());
            }
        }
        return result;
    }

    private CollectivityStructure resolveStructure(
            CreateCollectivityStructure req) {
        CollectivityStructure structure = new CollectivityStructure();
        try {
            if (req.getPresident() != null) {
                Member m = memberRepository.findById(req.getPresident());
                if (m == null) throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "President not found");
                structure.setPresident(m);
            }
            if (req.getVicePresident() != null) {
                Member m = memberRepository.findById(req.getVicePresident());
                if (m == null) throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vice president not found");
                structure.setVicePresident(m);
            }
            if (req.getTreasurer() != null) {
                Member m = memberRepository.findById(req.getTreasurer());
                if (m == null) throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Treasurer not found");
                structure.setTreasurer(m);
            }
            if (req.getSecretary() != null) {
                Member m = memberRepository.findById(req.getSecretary());
                if (m == null) throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Secretary not found");
                structure.setSecretary(m);
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }
        return structure;
    }

    public Collectivity assignIdentity(String collectivityId,
                                       AssignIdentityRequest request) {
        try {
            int id = Integer.parseInt(collectivityId);

            // Check collectivity exists
            Collectivity existing = collectivityRepository.findById(id);
            if (existing == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Collectivity not found");
            }

            // Check if number or name already assigned
            if (existing.getNumber() != null || existing.getName() != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Number and name already assigned and cannot be changed");
            }

            // Check if name already exists
            if (collectivityRepository.existsByName(request.getName())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Name already exists in another collectivity");
            }

            // Assign identity
            return collectivityRepository.assignIdentity(
                    id,
                    request.getNumber(),
                    request.getName()
            );

        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }
    }
}