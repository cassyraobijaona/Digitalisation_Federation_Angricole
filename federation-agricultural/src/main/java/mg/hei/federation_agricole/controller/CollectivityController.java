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
import java.time.LocalDate;
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

        List<String> ids = new java.util.ArrayList<>();

        for (CreateCollectivity c : list) {

            try (Connection conn = db.getConnection()) {

                conn.setAutoCommit(false);

                service.validate(c, conn);

                collectivityRepo.save(c.getId(),c.getLocation(),c.getSpecialization(), c.isFederationApproval() );

                roleRepo.assign(conn, c.getId(), c.getStructure().getPresident(), "PRESIDENT");
                roleRepo.assign(conn,c.getId() , c.getStructure().getVicePresident(), "VICE_PRESIDENT");
                roleRepo.assign(conn, c.getId(), c.getStructure().getTreasurer(), "TREASURER");
                roleRepo.assign(conn, c.getId(), c.getStructure().getSecretary(), "SECRETARY");

                conn.commit();

                ids.add(c.getId());

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return ResponseEntity.status(201).body(ids);
    }
    @PutMapping("/{id}/informations")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @RequestBody CollectivityInformation input) {

        try (Connection conn = db.getConnection()) {

            conn.setAutoCommit(false);

            Collectivity existing = collectivityRepo.findById( id);

            if (existing == null) {
                return ResponseEntity.status(404).body("Collectivity not found");
            }

            service.validateUpdate(existing, input);

            if (input.getName() != null) {
                existing.setName(input.getName());
            }

            if (input.getNumber() != null) {
                existing.setNumber(input.getNumber());
            }

            collectivityRepo.updateCollectivity( existing);

            conn.commit();

            Collectivity updated = collectivityRepo.findById( id);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
             // 🔥 IMPORTANT
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


    @GetMapping("/{id}/membershipFees")
    public List<MembershipFee> getFees(@PathVariable String id) {
        return service.getFees(id);
    }
    @PostMapping("/{id}/membershipFees")
    public List<MembershipFee> createFees(
            @PathVariable String id,
            @RequestBody List<MembershipFee> fees
    ) {
        return service.create(id, fees);
    }
    @GetMapping("/{id}/financialAccounts")
    public ResponseEntity<?> getFinancialAccounts(
            @PathVariable String id,
            @RequestParam LocalDate at
    ) {
        try {
            return ResponseEntity.ok(
                    service.getFinancialAccounts(id, at)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SERVER ERROR");
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getCollectivity(@PathVariable String id) {

        try {
            return ResponseEntity.ok(service.getCollectivityById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SERVER ERROR");
        }
    }
}