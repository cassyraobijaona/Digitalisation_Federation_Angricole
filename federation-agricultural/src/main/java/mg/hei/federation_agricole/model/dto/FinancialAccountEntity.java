package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.AccountType;
import mg.hei.federation_agricole.model.enums.OwnerType;

@Data
public class FinancialAccountEntity implements FinancialAccount {

    private String id;
    private OwnerType ownerType;
    private String collectivity_id;
    private AccountType accountType;
    private Double amount;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Double getAmount() {
        return amount;
    }

}
