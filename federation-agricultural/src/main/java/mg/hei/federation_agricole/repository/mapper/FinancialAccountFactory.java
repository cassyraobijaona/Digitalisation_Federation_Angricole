package mg.hei.federation_agricole.repository.mapper;


import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.model.enums.AccountType;
import mg.hei.federation_agricole.model.enums.OwnerType;

import java.sql.ResultSet;
import java.sql.SQLException;
public class FinancialAccountFactory {

    public static FinancialAccount map(ResultSet rs) throws SQLException {

        Integer id = rs.getInt("id");
        int ownerId = rs.getInt("owner_id");
        Double amount = rs.getDouble("amount");

        String ownerType = rs.getString("owner_type");
        String accountType = rs.getString("account_type");

        FinancialAccountEntity base = new FinancialAccountEntity();
        base.setId(id);
        base.setOwnerId(ownerId);
        base.setAmount(amount);
        base.setOwnerType(OwnerType.valueOf(ownerType));

        AccountType type = AccountType.valueOf(accountType);

        return switch (type) {

            case CASH -> {
                CashAccount c = new CashAccount();
                c.setId(base.getId());
                c.setOwnerId(base.getOwnerId());
                c.setOwnerType(base.getOwnerType());
                c.setAmount(base.getAmount());
                yield c;
            }

            case MOBILE_BANKING -> {
                MobileBankingAccount m = new MobileBankingAccount();
                m.setId(base.getId());
                m.setOwnerId(base.getOwnerId());
                m.setOwnerType(base.getOwnerType());
                m.setAmount(base.getAmount());
                yield m;
            }

            case BANK -> {
                BankAccount b = new BankAccount();
                b.setId(base.getId());
                b.setOwnerId(base.getOwnerId());
                b.setOwnerType(base.getOwnerType());
                b.setAmount(base.getAmount());
                yield b;
            }

            default -> throw new RuntimeException("Unknown account type");
        };
    }
}