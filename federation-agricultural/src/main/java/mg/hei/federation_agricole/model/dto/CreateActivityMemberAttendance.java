package mg.hei.federation_agricole.model.dto;

import lombok.Data;

@Data
public class CreateActivityMemberAttendance {
    private String memberIdentifier;
    private String attendanceStatus;
}