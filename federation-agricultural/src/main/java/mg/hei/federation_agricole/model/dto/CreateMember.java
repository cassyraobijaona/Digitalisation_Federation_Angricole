package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateMember extends MemberInformation {
    private String collectivityIdentifier;
    private List<String> referees;
    private Boolean registrationFeePaid;
    private Boolean membershipDuesPaid;
}