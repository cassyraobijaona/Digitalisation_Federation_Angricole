package mg.hei.federation_agricole.repository;

import mg.hei.federation_agricole.model.dto.CollectivityTransaction;
import mg.hei.federation_agricole.model.dto.MemberPayment;

import java.sql.Connection;
import java.util.List;

public interface TransactionRepository {
    void createFromPayment(Connection con, MemberPayment p);

    List<CollectivityTransaction> findByPeriod(int collectivityId, LocalDate from, LocalDate to);
}
