package mg.hei.federation_agricole.model.dto;

import lombok.Data;

@Data
public class MonthlyRecurrenceRule {
    private Integer weekOrdinal;
    private String dayOfWeek;
}