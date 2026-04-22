package mg.hei.federation_agricole.model.dto;

import lombok.Data;

@Data
public class CreateCollectivityStructure {
    private String president;
    private String vicePresident;
    private String treasurer;
    private String secretary;
}