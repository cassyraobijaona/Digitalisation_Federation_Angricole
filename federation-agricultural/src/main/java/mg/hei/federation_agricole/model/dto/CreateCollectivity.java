package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateCollectivity {
    private String id;
    private String location;
    private String specialization;
    private List<String> members;
    private Boolean federationApproval;
    private CreateCollectivityStructure structure;
    public boolean isFederationApproval() {
        return federationApproval;
    }

    public void setFederationApproval(boolean federationApproval) {
        this.federationApproval = federationApproval;
    }

}