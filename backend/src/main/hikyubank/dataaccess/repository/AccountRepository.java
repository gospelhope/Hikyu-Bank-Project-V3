package hikyubank.dataaccess.repository;

import hikyubank.application.model.Account;
import java.util.List;
import java.util.Optional;

/** Data access contract. A future database adapter implements this interface. */
public interface AccountRepository {
    List<Account> findAll();

    Optional<Account> findById(String id);

    Account save(Account item);
}
