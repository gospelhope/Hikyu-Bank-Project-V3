package hikyubank.web.controller;

import hikyubank.application.dto.BankDtos.*;
import hikyubank.application.model.Notification;
import hikyubank.application.service.NotificationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notifications;

    public NotificationController(NotificationService notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public List<Notification> list() {
        return notifications.list();
    }

    @PatchMapping("/{id}")
    public Notification update(
        @PathVariable String id,
        @Valid @RequestBody NotificationUpdate input
    ) {
        return notifications.update(id, input);
    }

    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead() {
        notifications.markAllRead();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String id) {
        notifications.remove(id);
    }

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.CREATED)
    public Notification createTest(@RequestBody TestNotificationRequest input) {
        return notifications.createTest(input.alertId());
    }
}
