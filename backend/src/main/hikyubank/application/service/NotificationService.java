package hikyubank.application.service;

import hikyubank.application.dto.BankDtos.NotificationUpdate;
import hikyubank.application.exception.BankException;
import hikyubank.application.model.Notification;
import hikyubank.dataaccess.repository.NotificationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final AlertService alerts;
    private final Clock clock;

    public NotificationService(
        NotificationRepository notifications,
        AlertService alerts,
        Clock clock
    ) {
        this.notifications = notifications;
        this.alerts = alerts;
        this.clock = clock;
    }

    public synchronized List<Notification> list() {
        return notifications.findAll().stream()
            .sorted(Comparator.comparing(Notification::createdAt).reversed())
            .toList();
    }

    public synchronized Notification update(String id, NotificationUpdate input) {
        var item = get(id);
        boolean resolved = input.resolved() == null ? item.resolved() : input.resolved();
        boolean read = input.read() == null ? item.read() : input.read();
        if (Boolean.TRUE.equals(input.resolved())) {
            read = true;
        }
        return notifications.save(item.withState(read, resolved));
    }

    public synchronized void markAllRead() {
        notifications.findAll().forEach(item ->
            notifications.save(item.withState(true, item.resolved()))
        );
    }

    public synchronized void remove(String id) {
        get(id);
        notifications.deleteById(id);
    }

    /** Explicit simulation endpoint; no SMS, email or calendar action is sent. */
    public synchronized Notification createTest(String alertId) {
        String title = "Welcome to your alert inbox";
        String body = "Your inbox is ready to store and manage reminders.";
        String accountId = null;
        if (alertId != null) {
            var rule = alerts.get(alertId);
            if (!rule.enabled()) {
                throw new BankException(409, "ALERT_DISABLED", "Enable this alert before testing it.");
            }
            title = "Test: " + rule.title();
            body = rule.date() == null
                ? "Threshold: $" + rule.amount() + "."
                : "Scheduled date: " + rule.date() + ".";
            accountId = rule.accountId();
        }
        return notifications.save(new Notification(
            UUID.randomUUID().toString(), alertId, accountId, title,
            body + " This is a simulated notification. No email or SMS was sent.",
            Instant.now(clock).toString(), false, false, "demo"
        ));
    }

    private Notification get(String id) {
        return notifications.findById(id).orElseThrow(() -> new BankException(
            404, "NOTIFICATION_NOT_FOUND", "Notification not found."
        ));
    }
}
