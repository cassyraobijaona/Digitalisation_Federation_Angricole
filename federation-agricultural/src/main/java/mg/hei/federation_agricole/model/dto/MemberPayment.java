package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.PaymentMode;

@Data
public class MemberPayment {
    private int id;
    private int memberId;
    private int membershipFeeId;
    private int accountId;
    private double amount;
    private PaymentMode paymentMode;
}