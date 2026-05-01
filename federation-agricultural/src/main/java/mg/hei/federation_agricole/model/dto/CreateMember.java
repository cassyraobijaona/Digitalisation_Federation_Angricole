package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mg.hei.federation_agricole.model.enums.Gender;
import mg.hei.federation_agricole.model.enums.MemberOccupation;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateMember extends MemberInformation {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;

    private String collectivityIdentifier;
    private List<String> referees;
    private String relation;
    private boolean registrationFeePaid;
    private boolean membershipDuesPaid;
}