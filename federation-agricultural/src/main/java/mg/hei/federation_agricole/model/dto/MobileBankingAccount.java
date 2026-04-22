package mg.hei.federation_agricole.model.dto;

import mg.hei.federation_agricole.model.enums.MobileBankingService;

public class MobileBankingAccount extends FinancialAccountEntity {

    private String holderName;
    private String mobileNumber;
    private MobileBankingService service;

}
