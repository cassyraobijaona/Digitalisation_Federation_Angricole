package mg.hei.federation_agricole.model.dto;

import lombok.Data;

@Data
public class CollectivityStructure {
    private Member president;
    private Member vicePresident;
    private Member treasurer;
    private Member secretary;
}