package hikyubank.application.service;

import hikyubank.application.dto.BankDtos.AlertRequest;
import hikyubank.application.exception.BankException;
import hikyubank.application.model.Alert;
import hikyubank.dataaccess.repository.AlertRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
    private static final Set<String> AMOUNT_TYPES = Set.of(
        "low_balance", "large_transaction", "savings_goal"
    );
    private static final Set<String> DATE_TYPES = Set.of("payment_due", "scheduled");
    private final AlertRepository alerts;
    private final AccountService accounts;

    public AlertService(AlertRepository alerts, AccountService accounts) {
        this.alerts = alerts;
        this.accounts = accounts;
    }

    public List<Alert> list() {
        return alerts.findAll();
    }

    public Alert get(String id) {
        return alerts.findById(id).orElseThrow(() -> new BankException(
            404, "ALERT_NOT_FOUND", "Alert rule not found."
        ));
    }

    public Alert create(AlertRequest input) {
        if (!AMOUNT_TYPES.contains(input.type()) && !DATE_TYPES.contains(input.type())) {
            throw invalid("Choose a valid alert type.");
        }
        accounts.get(input.accountId());
        BigDecimal amount = null;
        String date = null;
        if (AMOUNT_TYPES.contains(input.type())) {
            amount = input.amount();
            if (amount == null || amount.compareTo(new BigDecimal("0.01")) < 0
                || amount.compareTo(new BigDecimal("1000000")) > 0) {
                throw invalid("Enter an amount between $0.01 and $1,000,000.");
            }
        } else {
            try {
                date = LocalDate.parse(input.date() == null ? "" : input.date()).toString();
            } catch (DateTimeParseException error) {
                throw invalid("Choose a valid date.");
            }
        }
        return alerts.save(new Alert(
            UUID.randomUUID().toString(), input.type(), input.accountId(),
            input.title().trim(), input.channel(), amount, date, true
        ));
    }

    public synchronized Alert toggle(String id, boolean enabled) {
        return alerts.save(get(id).withEnabled(enabled));
    }

    private BankException invalid(String message) {
        return new BankException(400, "INVALID_ALERT", message);
    }
}
