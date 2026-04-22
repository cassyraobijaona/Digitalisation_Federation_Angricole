package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class Collectivity {
    private String id;
    private String location;
    private CollectivityStructure structure;
    private List<Member> members;
}