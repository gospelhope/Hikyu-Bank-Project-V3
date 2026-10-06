package hikyubank.dataaccess.memory;

import hikyubank.application.model.Notification;
import hikyubank.dataaccess.repository.NotificationRepository;
import hikyubank.storage.MemoryDatabase;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryNotificationRepository implements NotificationRepository {
    private final MemoryDatabase database;

    public MemoryNotificationRepository(MemoryDatabase database) {
        this.database = database;
    }

    @Override
    public List<Notification> findAll() {
        synchronized (database.notifications()) {
            return List.copyOf(database.notifications().values());
        }
    }

    @Override
    public Optional<Notification> findById(String id) {
        synchronized (database.notifications()) {
            return Optional.ofNullable(database.notifications().get(id));
        }
    }

    @Override
    public Notification save(Notification item) {
        synchronized (database.notifications()) {
            database.notifications().put(item.id(), item);
            return item;
        }
    }

    @Override
    public void deleteById(String id) {
        synchronized (database.notifications()) {
            database.notifications().remove(id);
        }
    }
}
