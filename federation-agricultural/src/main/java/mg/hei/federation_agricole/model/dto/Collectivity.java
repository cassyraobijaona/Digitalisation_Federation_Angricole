package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class Collectivity {
    private int id;
    private String location;
    private boolean federationApproval;
    private String name;   // IMMUTABLE
    private Integer number;
    private List<Member> members;

    private CollectivityStructure structure;
}