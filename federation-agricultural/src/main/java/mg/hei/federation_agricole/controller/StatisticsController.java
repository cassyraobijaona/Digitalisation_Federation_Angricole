package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class StatisticsController {

    private final StatisticsService service;
    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    @GetMapping("/collectivities/statistics")
    public ResponseEntity<?> getOverallStatistics(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        try {
            return ResponseEntity.ok(service.getOverallStatistics(from, to));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SERVER ERROR: " + e.getMessage());
        }
    }
}
