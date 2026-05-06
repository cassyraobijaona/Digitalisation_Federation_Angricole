package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<?> createActivities(
            @PathVariable String id,
            @RequestBody List<CreateCollectivityActivity> activities) {
        try {
            return ResponseEntity.ok(
                    activityService.createActivities(id, activities));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<?> getActivities(@PathVariable String id) {
        try {
            return ResponseEntity.ok(activityService.getActivities(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<?> createAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<CreateActivityMemberAttendance> attendances) {
        try {
            return ResponseEntity.status(201).body(
                    activityService.createAttendance(activityId, attendances));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<?> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {
        try {
            return ResponseEntity.ok(
                    activityService.getAttendance(activityId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}