package mg.hei.federation_agricole.repository.mapper;


import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.model.enums.AccountType;
import mg.hei.federation_agricole.model.enums.Bank;
import mg.hei.federation_agricole.model.enums.MobileBankingService;
import mg.hei.federation_agricole.model.enums.OwnerType;

import java.sql.ResultSet;
import java.sql.SQLException;
public class FinancialAccountFactory {

        public static FinancialAccount map(ResultSet rs) throws SQLException {

            Integer accountId = rs.getInt("fa_id");

            // BANK
            if (rs.getString("bank_holder") != null) {
                BankAccount b = new BankAccount();
                b.setId(accountId);
                b.setAmount(rs.getDouble("fa_amount"));
                b.setHolderName(rs.getString("bank_holder"));
                b.setBankName(Bank.valueOf(rs.getString("bank_name")));
                b.setBankCode(rs.getInt("bank_code"));
                b.setBankBranchCode(rs.getInt("bank_branch_code"));
                b.setBankAccountNumber(rs.getLong("bank_account_number"));
                b.setBankAccountKey(rs.getInt("bank_account_key"));
                return b;
            }

            // MOBILE
            if (rs.getString("mobile_holder") != null) {
                MobileBankingAccount m = new MobileBankingAccount();
                m.setId(accountId);
                m.setAmount(rs.getDouble("fa_amount"));
                return m;
            }

            // CASH (par défaut)
            CashAccount c = new CashAccount();
            c.setId(accountId);
            c.setAmount(rs.getDouble("fa_amount"));

            return c;
        }
    }
