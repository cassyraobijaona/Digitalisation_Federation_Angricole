package mg.hei.federation_agricole.controller;
import mg.hei.federation_agricole.config.DatabaseConnection;

import mg.hei.federation_agricole.model.dto.CreateCollectivity;
import mg.hei.federation_agricole.repository.CollectivityRepository;
import mg.hei.federation_agricole.repository.RoleAssignmentRepository;
import mg.hei.federation_agricole.service.CollectivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;

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
}