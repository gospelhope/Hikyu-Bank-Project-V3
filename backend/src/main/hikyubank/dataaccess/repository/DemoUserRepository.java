package hikyubank.dataaccess.repository;

import hikyubank.application.model.DemoUser;
import java.util.List;
import java.util.Optional;

/** Data access contract. A future database adapter implements this interface. */
public interface DemoUserRepository {
    List<DemoUser> findAll();

    Optional<DemoUser> findByUsername(String id);

    DemoUser save(DemoUser item);
}
