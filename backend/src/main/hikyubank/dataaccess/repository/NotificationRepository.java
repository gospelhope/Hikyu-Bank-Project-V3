package hikyubank.dataaccess.repository;

import hikyubank.application.model.Notification;
import java.util.List;
import java.util.Optional;

/** Data access contract. A future database adapter implements this interface. */
public interface NotificationRepository {
    List<Notification> findAll();

    Optional<Notification> findById(String id);

    Notification save(Notification item);

    void deleteById(String id);
}
