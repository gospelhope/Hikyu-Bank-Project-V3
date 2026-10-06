package hikyubank.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import hikyubank.application.model.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Storage tier for the local demonstration, not a SQL database.
 * All records reset on restart. Only repository adapters access these maps.
 */
@Component
public class MemoryDatabase {
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final Map<String, Transaction> transactions = new LinkedHashMap<>();
    private final Map<String, Alert> alerts = new LinkedHashMap<>();
    private final Map<String, Notification> notifications = new LinkedHashMap<>();
    private final Map<String, DemoUser> users = new LinkedHashMap<>();

    public MemoryDatabase(ObjectMapper mapper) throws IOException {
        try (var stream = new ClassPathResource("seed/demo-data.json").getInputStream()) {
            var seed = mapper.readValue(stream, SeedData.class);
            seed.accounts().forEach(item -> accounts.put(item.id(), item));
            seed.transactions().forEach(item -> transactions.put(item.id(), item));
            seed.alerts().forEach(item -> alerts.put(item.id(), item));
        }
    }

    public Map<String, Account> accounts() {
        return accounts;
    }

    public Map<String, Transaction> transactions() {
        return transactions;
    }

    public Map<String, Alert> alerts() {
        return alerts;
    }

    public Map<String, Notification> notifications() {
        return notifications;
    }

    public Map<String, DemoUser> users() {
        return users;
    }

    public record SeedData(
        List<Account> accounts,
        List<Transaction> transactions,
        List<Alert> alerts
    ) {}
}
