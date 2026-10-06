package hikyubank.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/api/v1/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "in-memory", true);
    }

    public record HealthResponse(String status, String storage, boolean demo) {}
}
