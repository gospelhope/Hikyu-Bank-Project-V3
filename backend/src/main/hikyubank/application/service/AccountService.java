package hikyubank.application.service;

import hikyubank.application.dto.BankDtos.OpenAccountRequest;
import hikyubank.application.dto.BankDtos.AccountProduct;
import hikyubank.application.exception.BankException;
import hikyubank.application.model.Account;
import hikyubank.dataaccess.repository.AccountRepository;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accounts;
    private final SecureRandom random = new SecureRandom();

    public AccountService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public List<Account> list() {
        return accounts.findAll();
    }

    public Account get(String id) {
        return accounts.findById(id).orElseThrow(() -> new BankException(
            404, "ACCOUNT_NOT_FOUND", "Account not found."
        ));
    }

    public List<AccountProduct> products() {
        return List.of(
            product("checking", "Checking", "A place for everyday money.", "▤"),
            product("savings", "Savings", "Give your next goal a home.", "◇"),
            product("credit", "Credit card", "Explore a simulated credit account.", "▱"),
            product("loan", "Loan", "Start a demo loan application.", "⌂"),
            product("investment", "Investment", "Explore a demo investment account.", "↗")
        );
    }

    private AccountProduct product(String type, String name, String description, String icon) {
        return new AccountProduct(type, name, description, icon, alreadyOpen(type));
    }

    private boolean alreadyOpen(String type) {
        return Set.of("checking", "savings").contains(type)
            && list().stream().anyMatch(account -> account.type().equals(type)
                && Set.of("active", "pending").contains(account.status()));
    }

    /** Serialize duplicate checks with insertion for this single-instance demo. */
    public synchronized Account open(OpenAccountRequest input) {
        boolean restricted = Set.of("checking", "savings").contains(input.type());
        if (alreadyOpen(input.type())) {
            throw new BankException(
                409, "ACCOUNT_ALREADY_OPEN", "You have already opened this account."
            );
        }
        String status = restricted ? "active" : "pending";
        return accounts.save(new Account(
            UUID.randomUUID().toString(), input.type(), input.name().trim(),
            String.valueOf(1000 + random.nextInt(9000)), BigDecimal.ZERO,
            status, null, null, null
        ));
    }
}
