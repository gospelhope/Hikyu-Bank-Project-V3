package hikyubank.dataaccess.memory;

import hikyubank.application.model.Transaction;
import hikyubank.dataaccess.repository.TransactionRepository;
import hikyubank.storage.MemoryDatabase;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryTransactionRepository implements TransactionRepository {
    private final MemoryDatabase database;

    public MemoryTransactionRepository(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public List<Transaction> findAll() {
        synchronized (database.transactions()) {
            return List.copyOf(database.transactions().values());
        }
    }

    @Override
    public Optional<Transaction> findById(String id) {
        synchronized (database.transactions()) {
            return Optional.ofNullable(database.transactions().get(id));
        }
    }

    @Override
    public Transaction save(Transaction item) {
        synchronized (database.transactions()) {
            database.transactions().put(item.id(), item);
            return item;
        }
    }
}
