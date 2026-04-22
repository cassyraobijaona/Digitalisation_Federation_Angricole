package mg.hei.federation_agricole.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import mg.hei.federation_agricole.model.enums.Gender;
import mg.hei.federation_agricole.model.enums.MemberOccupation;
import java.time.LocalDate;

@JsonFormat(pattern = "yyyy-MM-dd")
@Data
public class MemberInformation {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;
}