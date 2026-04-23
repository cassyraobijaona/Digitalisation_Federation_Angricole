package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.ActivityStatus;
import mg.hei.federation_agricole.model.enums.Frequency;

import java.time.LocalDate;
@Data
public class MembershipFee {
    private Integer id;
    private LocalDate eligibleFrom;
    private Frequency frequency;
    private double amount;
    private String label;
    private ActivityStatus status;
}
