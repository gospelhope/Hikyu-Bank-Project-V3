package hikyubank.dataaccess.repository;

import hikyubank.application.model.Alert;
import java.util.List;
import java.util.Optional;

/** Data access contract. A future database adapter implements this interface. */
public interface AlertRepository {
    List<Alert> findAll();

    Optional<Alert> findById(String id);

    Alert save(Alert item);
}
