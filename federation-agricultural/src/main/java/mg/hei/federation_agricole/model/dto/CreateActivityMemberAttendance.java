package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.AttendanceStatus;

@Data
public class CreateActivityMemberAttendance {
    private String id;
    private String memberIdentifier;
    private AttendanceStatus attendanceStatus;
}
