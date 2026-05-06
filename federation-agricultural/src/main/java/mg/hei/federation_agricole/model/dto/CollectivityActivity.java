package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CollectivityActivity {
    private String id;
    private String label;
    private String activityType;
    private List<String> memberOccupationConcerned;
    private MonthlyRecurrenceRule recurrenceRule;
    private LocalDate executiveDate;
}