package mg.hei.federation_agricole.service;

import mg.hei.federation_agricole.model.dto.FinancialAccount;
import mg.hei.federation_agricole.model.dto.FinancialAccountEntity;
import mg.hei.federation_agricole.repository.FinancialAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class FinancialAccountService {

    private final FinancialAccountRepository repo;

    public FinancialAccountService(FinancialAccountRepository repo) {
        this.repo = repo;
    }

    public FinancialAccount createAccount(FinancialAccountEntity account) {
        return repo.save(account);
    }

    public FinancialAccount getAccount(Integer id) {
        return repo.findById(id);
    }
}
