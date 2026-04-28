package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.Bank;

@Data
public class Referee {
    private String memberId;
    private String refereeId;
    private String relation;

    public static class BankAccount extends FinancialAccountEntity {

        private String holderName;
        private Bank bankName;

        private Integer bankCode;
        private Integer bankBranchCode;
        private long bankAccountNumber;
        private Integer bankAccountKey;
    }
}
