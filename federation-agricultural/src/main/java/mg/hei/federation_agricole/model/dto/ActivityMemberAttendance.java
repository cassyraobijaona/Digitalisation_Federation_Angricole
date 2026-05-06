package mg.hei.federation_agricole.model.dto;

import lombok.Data;

@Data
public class ActivityMemberAttendance {
    private String id;
    private MemberDescription memberDescription;
    private String attendanceStatus;
}