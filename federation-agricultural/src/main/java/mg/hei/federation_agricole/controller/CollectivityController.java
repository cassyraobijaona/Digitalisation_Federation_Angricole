package mg.hei.federation_agricole.controller;
import mg.hei.federation_agricole.config.DatabaseConnection;

import mg.hei.federation_agricole.model.dto.Collectivity;
import mg.hei.federation_agricole.model.dto.CollectivityInformation;
import mg.hei.federation_agricole.model.dto.CreateCollectivity;
import mg.hei.federation_agricole.model.dto.MembershipFee;
import mg.hei.federation_agricole.repository.CollectivityRepository;
import mg.hei.federation_agricole.repository.RoleAssignmentRepository;
import mg.hei.federation_agricole.service.CollectivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final DatabaseConnection db;
    private final CollectivityRepository collectivityRepo;
    private final RoleAssignmentRepository roleRepo;
    private final CollectivityService service;

    public CollectivityController(DatabaseConnection db,
                                  CollectivityRepository collectivityRepo,
                                  RoleAssignmentRepository roleRepo,
                                  CollectivityService service) {
        this.db = db;
        this.collectivityRepo = collectivityRepo;
        this.roleRepo = roleRepo;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody java.util.List<CreateCollectivity> list) {

        java.util.List<Integer> ids = new java.util.ArrayList<>();

        for (CreateCollectivity c : list) {

            try (Connection conn = db.getConnection()) {

                conn.setAutoCommit(false);

                service.validate(c, conn);

                int id = collectivityRepo.save(conn, c.getLocation(), c.isFederationApproval());

                roleRepo.assign(conn, id, Integer.parseInt(c.getStructure().getPresident()), "PRESIDENT");
                roleRepo.assign(conn, id, Integer.parseInt(c.getStructure().getVicePresident()), "VICE_PRESIDENT");
                roleRepo.assign(conn, id, Integer.parseInt(c.getStructure().getTreasurer()), "TREASURER");
                roleRepo.assign(conn, id, Integer.parseInt(c.getStructure().getSecretary()), "SECRETARY");

                conn.commit();

                ids.add(id);

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return ResponseEntity.status(201).body(ids);
    }
    @PutMapping("/{id}/informations")
    public ResponseEntity<?> update(@PathVariable int id,
                                    @RequestBody CollectivityInformation input) {

        try (Connection conn = db.getConnection()) {

            Collectivity existing = collectivityRepo.findById(conn, id);

            if (existing == null) {
                return ResponseEntity.status(404).body("Collectivity not found");
            }

            service.validateUpdate(existing, input);

            collectivityRepo.updateInfo(conn, id,
                    input.getName() != null ? input.getName() : existing.getName(),
                    true
            );

            return ResponseEntity.ok(existing);

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SERVER ERROR");
        }
    }


    @GetMapping("/{id}/membershipFees")
    public List<MembershipFee> getFees(@PathVariable int id) {
        return service.getFees(id);
    }
}