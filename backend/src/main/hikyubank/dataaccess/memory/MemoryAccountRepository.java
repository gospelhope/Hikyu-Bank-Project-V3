package hikyubank.dataaccess.memory;

import hikyubank.application.model.Account;
import hikyubank.dataaccess.repository.AccountRepository;
import hikyubank.storage.MemoryDatabase;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryAccountRepository implements AccountRepository {
    private final MemoryDatabase database;

    public MemoryAccountRepository(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public List<Account> findAll() {
        synchronized (database.accounts()) {
            return List.copyOf(database.accounts().values());
        }
    }

    @Override
    public Optional<Account> findById(String id) {
        synchronized (database.accounts()) {
            return Optional.ofNullable(database.accounts().get(id));
        }
    }

    @Override
    public Account save(Account item) {
        synchronized (database.accounts()) {
            database.accounts().put(item.id(), item);
            return item;
        }
    }
}
