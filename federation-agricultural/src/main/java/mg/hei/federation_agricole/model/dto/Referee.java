package mg.hei.federation_agricole.model.dto;

import lombok.Data;

@Data
public class Referee {
    private int memberId;
    private int refereeId;
    private String relation;
}
