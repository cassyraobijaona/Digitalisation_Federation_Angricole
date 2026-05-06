package mg.hei.federation_agricole.model.dto;


import lombok.Data;
import mg.hei.federation_agricole.model.enums.ActivityType;
import mg.hei.federation_agricole.model.enums.MemberOccupation;

import java.time.LocalDate;
import java.util.List;

@Data
public class CollectivityActivity {
    private String id;
    private String label;
    private ActivityType activityType;
    private List<MemberOccupation> memberOccupationConcerned;
    private MonthlyRecurrenceRule recurrenceRule;
    private LocalDate executiveDate;
}
