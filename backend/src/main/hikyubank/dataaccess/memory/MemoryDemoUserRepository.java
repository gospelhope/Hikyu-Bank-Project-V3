package hikyubank.dataaccess.memory;

import hikyubank.application.model.DemoUser;
import hikyubank.dataaccess.repository.DemoUserRepository;
import hikyubank.storage.MemoryDatabase;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryDemoUserRepository implements DemoUserRepository {
    private final MemoryDatabase database;

    public MemoryDemoUserRepository(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public List<DemoUser> findAll() {
        synchronized (database.users()) {
            return List.copyOf(database.users().values());
        }
    }

    @Override
    public Optional<DemoUser> findByUsername(String id) {
        synchronized (database.users()) {
            return Optional.ofNullable(database.users().get(id));
        }
    }

    @Override
    public DemoUser save(DemoUser item) {
        synchronized (database.users()) {
            database.users().put(item.username(), item);
            return item;
        }
    }
}
