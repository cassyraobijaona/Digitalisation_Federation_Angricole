package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Member extends MemberInformation {
    private String id;
    private List<Member> referees;
}