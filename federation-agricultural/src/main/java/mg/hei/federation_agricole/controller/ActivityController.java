package mg.hei.federation_agricole.controller;


import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class ActivityController {

    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<?> create(@PathVariable String id,
                                    @RequestBody List<CreateCollectivityActivity> list) {
        try {
            return ResponseEntity.ok(service.create(id, list));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found"))
                return ResponseEntity.status(404).body(e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SERVER ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<?> getActivities(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.getActivities(id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found"))
                return ResponseEntity.status(404).body(e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SERVER ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<?> saveAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<CreateActivityMemberAttendance> list) {
        try {
            return ResponseEntity.status(201)
                    .body(service.saveAttendance(id, activityId, list));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found"))
                return ResponseEntity.status(404).body(e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SERVER ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<?> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {
        try {
            return ResponseEntity.ok(service.getAttendance(id, activityId));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found"))
                return ResponseEntity.status(404).body(e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SERVER ERROR: " + e.getMessage());
        }
    }
}