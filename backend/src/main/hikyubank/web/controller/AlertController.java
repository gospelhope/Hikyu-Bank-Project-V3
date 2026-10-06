package hikyubank.web.controller;

import hikyubank.application.dto.BankDtos.*;
import hikyubank.application.model.Alert;
import hikyubank.application.service.AlertService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {
    private final AlertService alerts;

    public AlertController(AlertService alerts) {
        this.alerts = alerts;
    }

    @GetMapping
    public List<Alert> list() {
        return alerts.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Alert create(@Valid @RequestBody AlertRequest input) {
        return alerts.create(input);
    }

    @PatchMapping("/{id}")
    public Alert toggle(
        @PathVariable String id,
        @Valid @RequestBody ToggleAlertRequest input
    ) {
        return alerts.toggle(id, input.enabled());
    }
}
