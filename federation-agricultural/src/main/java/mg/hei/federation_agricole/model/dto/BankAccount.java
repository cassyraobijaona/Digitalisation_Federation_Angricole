package mg.hei.federation_agricole.model.dto;

import mg.hei.federation_agricole.model.enums.Bank;

public class BankAccount extends FinancialAccountEntity {

    private String holderName;
    private Bank bankName;

    private Integer bankCode;
    private Integer bankBranchCode;
    private Long bankAccountNumber;
    private Integer bankAccountKey;
}