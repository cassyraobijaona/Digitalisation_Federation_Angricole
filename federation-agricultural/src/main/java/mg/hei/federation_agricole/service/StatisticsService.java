package mg.hei.federation_agricole.service;

import mg.hei.federation_agricole.model.dto.CollectivityOverallStatistics;
import mg.hei.federation_agricole.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsRepository statsRepo;

    public StatisticsService(StatisticsRepository statsRepo) {
        this.statsRepo = statsRepo;
    }

    public List<CollectivityOverallStatistics> getOverallStatistics(
            LocalDate from, LocalDate to) {

        if (from.isAfter(to))
            throw new RuntimeException("'from' must be before 'to'");

        return statsRepo.findOverallStatistics(from, to);
    }
}
