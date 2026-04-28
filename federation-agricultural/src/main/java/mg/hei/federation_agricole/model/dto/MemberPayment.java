package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.PaymentMode;

@Data
public class MemberPayment {
    private String id;
    private String memberId;
    private String membershipFeeIdentifier;
    private String accountCreditedIdentifier;
    private Double amount;
    private PaymentMode paymentMode;
}