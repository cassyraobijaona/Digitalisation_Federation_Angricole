package mg.hei.federation_agricole.repository.mapper;


import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.model.enums.AccountType;
import mg.hei.federation_agricole.model.enums.OwnerType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FinancialAccountFactory {

    public static FinancialAccount map(ResultSet rs) throws SQLException {

        AccountType type = AccountType.valueOf(rs.getString("account_type"));

        FinancialAccountEntity base = new FinancialAccountEntity();
        base.setId(rs.getInt("id"));
        base.setAmount(rs.getDouble("amount"));
        base.setOwnerId(rs.getInt("owner_id"));
        base.setOwnerType(OwnerType.valueOf(rs.getString("owner_type")));
        base.setAccountType(type);

        switch (type) {

            case CASH:
                return new CashAccount();

            case MOBILE_BANKING:
                MobileBankingAccount m = new MobileBankingAccount();
                m.setId(base.getId());
                m.setAmount(base.getAmount());
                return m;

            case BANK:
                BankAccount b = new BankAccount();
                b.setId(base.getId());
                b.setAmount(base.getAmount());
                return b;

            default:
                throw new RuntimeException("Unknown account type");
        }
    }
}
