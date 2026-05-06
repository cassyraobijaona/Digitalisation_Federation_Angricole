package mg.hei.federation_agricole.model.dto;

import lombok.Data;

@Data
public class CollectivityOverallStatistics {
    private CollectivityInformation collectivityInformation;
    private Integer newMembersNumber;
    private Double overallMemberCurrentDuePercentage;
}
