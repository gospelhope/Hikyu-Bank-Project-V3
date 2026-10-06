package hikyubank.dataaccess.repository;

import hikyubank.application.model.Transaction;
import java.util.List;
import java.util.Optional;

/** Data access contract. A future database adapter implements this interface. */
public interface TransactionRepository {
    List<Transaction> findAll();

    Optional<Transaction> findById(String id);

    Transaction save(Transaction item);
}
