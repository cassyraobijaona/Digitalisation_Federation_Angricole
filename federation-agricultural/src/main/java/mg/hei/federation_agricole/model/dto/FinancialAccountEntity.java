package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.AccountType;
import mg.hei.federation_agricole.model.enums.OwnerType;

@Data
public class FinancialAccountEntity implements FinancialAccount {

    private Integer id;
    private OwnerType ownerType;
    private Integer ownerId;
    private AccountType accountType;
    private Double amount;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Double getAmount() {
        return amount;
    }

}
