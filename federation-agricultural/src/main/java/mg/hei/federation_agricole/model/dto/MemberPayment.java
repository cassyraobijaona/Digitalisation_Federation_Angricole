package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.PaymentMode;

@Data
public class MemberPayment {
    private Integer id;
    private Integer memberId;
    private Integer membershipFeeId;
    private Integer accountId;
    private Double amount;
    private PaymentMode paymentMode;
}