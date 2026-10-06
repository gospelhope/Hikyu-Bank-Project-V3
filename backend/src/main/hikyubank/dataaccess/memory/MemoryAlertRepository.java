package hikyubank.dataaccess.memory;

import hikyubank.application.model.Alert;
import hikyubank.dataaccess.repository.AlertRepository;
import hikyubank.storage.MemoryDatabase;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryAlertRepository implements AlertRepository {
    private final MemoryDatabase database;

    public MemoryAlertRepository(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public List<Alert> findAll() {
        synchronized (database.alerts()) {
            return List.copyOf(database.alerts().values());
        }
    }

    @Override
    public Optional<Alert> findById(String id) {
        synchronized (database.alerts()) {
            return Optional.ofNullable(database.alerts().get(id));
        }
    }

    @Override
    public Alert save(Alert item) {
        synchronized (database.alerts()) {
            database.alerts().put(item.id(), item);
            return item;
        }
    }
}
