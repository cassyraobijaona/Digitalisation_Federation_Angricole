package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.model.dto.CollectivityLocalStatistics;
import mg.hei.federation_agricole.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/collectivites/statistics")
    public ResponseEntity<?> getOverallStatistics(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        try {
            return ResponseEntity.ok(
                    statisticsService.getOverallStatistics(from, to));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("SERVER ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/collectivites/{id}/statistics")
    public ResponseEntity<?> getLocalStatistics(
            @PathVariable String id,
            @RequestParam String from,
            @RequestParam String to) {
        try {
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);
            List<CollectivityLocalStatistics> stats =
                    statisticsService.getLocalStatistics(id, fromDate, toDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}