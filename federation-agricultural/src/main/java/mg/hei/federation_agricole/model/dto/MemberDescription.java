package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.MemberOccupation;

@Data
public class MemberDescription {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private MemberOccupation occupation;
}
