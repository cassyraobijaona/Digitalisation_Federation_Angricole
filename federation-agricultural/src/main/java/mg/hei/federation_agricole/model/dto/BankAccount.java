package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mg.hei.federation_agricole.model.enums.Bank;
@Getter
@Setter

public class BankAccount extends FinancialAccountEntity {

    private String holderName;
    private Bank bankName;

    private Integer bankCode;
    private Integer bankBranchCode;
    private Long bankAccountNumber;
    private Integer bankAccountKey;
}