package mg.hei.federation_agricole.controller;
import mg.hei.federation_agricole.config.DatabaseConnection;

import mg.hei.federation_agricole.exception.BadRequestException;
import mg.hei.federation_agricole.exception.NotFoundException;
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
import java.util.ArrayList;
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
    public ResponseEntity<?> create(@RequestBody List<CreateCollectivity> list) {

        if (list == null || list.isEmpty())
            throw new BadRequestException("List cannot be empty");

        List<String> ids = new ArrayList<>();

        for (CreateCollectivity c : list) {
            try (Connection conn = db.getConnection()) {
                conn.setAutoCommit(false);
                service.validate(c, conn);
                collectivityRepo.save(c.getId(), c.getLocation(), c.getSpecialization(), c.isFederationApproval());
                roleRepo.assign(conn, c.getId(), c.getStructure().getPresident(), "PRESIDENT");
                roleRepo.assign(conn, c.getId(), c.getStructure().getVicePresident(), "VICE_PRESIDENT");
                roleRepo.assign(conn, c.getId(), c.getStructure().getTreasurer(), "TREASURER");
                roleRepo.assign(conn, c.getId(), c.getStructure().getSecretary(), "SECRETARY");
                conn.commit();
                ids.add(c.getId());
            } catch (BadRequestException e) {
                throw e; // → 400
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage()); // → 500
            }
        }

        return ResponseEntity.status(201).body(ids);
    }

    @PutMapping("/{id}/informations")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @RequestBody CollectivityInformation input) {
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            Collectivity existing = collectivityRepo.findById(id);
            if (existing == null)
                throw new NotFoundException("Collectivity not found: " + id);

            service.validateUpdate(existing, input);

            if (input.getName() != null) existing.setName(input.getName());
            if (input.getNumber() != null) existing.setNumber(input.getNumber());

            collectivityRepo.updateCollectivity(existing);
            conn.commit();

            return ResponseEntity.ok(collectivityRepo.findById(id)); // 200
        } catch (NotFoundException | BadRequestException e) {
            throw e; // → 404 ou 400
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage()); // → 500
        }
    }

    @GetMapping("/{id}/membershipFees")
    public ResponseEntity<?> getFees(@PathVariable String id) {
        Collectivity existing = null;
        try { existing = collectivityRepo.findById(id); } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        if (existing == null)
            throw new NotFoundException("Collectivity not found: " + id);

        return ResponseEntity.ok(service.getFees(id)); // 200
    }

    @PostMapping("/{id}/membershipFees")
    public ResponseEntity<?> createFees(@PathVariable String id,
                                        @RequestBody List<MembershipFee> fees) {
        if (fees == null || fees.isEmpty())
            throw new BadRequestException("Fees list cannot be empty");

        Collectivity existing = null;
        try { existing = collectivityRepo.findById(id); } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        if (existing == null)
            throw new NotFoundException("Collectivity not found: " + id);

        return ResponseEntity.status(201).body(service.create(id, fees)); // 201
    }

    @GetMapping("/{id}/financialAccounts")
    public ResponseEntity<?> getFinancialAccounts(@PathVariable String id,
                                                  @RequestParam LocalDate at) {
        if (at == null)
            throw new BadRequestException("Parameter 'at' is required");

        Collectivity existing = null;
        try { existing = collectivityRepo.findById(id); } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        if (existing == null)
            throw new NotFoundException("Collectivity not found: " + id);

        return ResponseEntity.ok(service.getFinancialAccounts(id, at)); // 200
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCollectivity(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.getCollectivityById(id)); // 200
        } catch (RuntimeException e) {
            throw new NotFoundException(e.getMessage()); // 404
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage()); // 500
        }
    }
}