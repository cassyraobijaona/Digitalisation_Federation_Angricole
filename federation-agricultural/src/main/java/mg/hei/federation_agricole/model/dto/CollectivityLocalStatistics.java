package mg.hei.federation_agricole.model.dto;

import lombok.Data;

@Data
public class CollectivityLocalStatistics {
    private MemberDescription memberDescription;
    private Double earnedAmount;
    private Double unpaidAmount;
}