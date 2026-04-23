package mg.hei.federation_agricole.service;

import mg.hei.federation_agricole.model.dto.CollectivityTransaction;
import mg.hei.federation_agricole.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repo;

    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }

    public List<CollectivityTransaction> getTransactions(Integer id, LocalDate from, LocalDate to) {
        return repo.findByPeriod(id, from, to);
    }
}
