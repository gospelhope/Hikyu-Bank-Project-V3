package hikyubank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hikyubank.storage.MemoryDatabase;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

/** Real controller -> service -> repository -> storage integration tests. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(BankApiIntegrationTest.ClockConfiguration.class)
class BankApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired MemoryDatabase storage;
    @Autowired MutableClock clock;

    @BeforeEach
    void resetDemoState() {
        clock.reset();
        storage.accounts().keySet().removeIf(id -> !Set.of("checking", "savings", "credit").contains(id));
        storage.alerts().keySet().removeIf(id -> !Set.of("r1", "r2", "r3").contains(id));
        storage.alerts().replaceAll((id, alert) -> alert.withEnabled(!id.equals("r3")));
        storage.notifications().clear();
    }

    @Test
    void protectedEndpointsRejectAnonymousRequests() throws Exception {
        for (String path : new String[]{"accounts", "transactions", "alerts", "notifications"}) {
            var result = call("GET", "/" + path, null, new MockHttpSession());
            assertEquals(401, result.getResponse().getStatus());
        }
        assertEquals(200, call("GET", "/health", null, null).getResponse().getStatus());
    }

    @Test
    void writesRequireApplicationHeader() throws Exception {
        var result = mvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(credentials()))).andReturn();
        assertEquals(403, result.getResponse().getStatus());
    }

    @Test
    void smsVerificationCreatesSessionAndConsumesChallenge() throws Exception {
        var session = new MockHttpSession();
        var challenge = json(call("POST", "/auth/login", credentials(), session));
        var code = requestCode(session, challenge, "sms");
        var payload = verification(challenge, code, "sms", "");
        assertEquals(200, call("POST", "/auth/verify", payload, session).getResponse().getStatus());
        var profile = json(call("GET", "/auth/session", null, session));
        assertEquals("Chengyang Lee", profile.path("user").path("name").asText());
        assertEquals("Lee", profile.path("user").path("lastName").asText());
        var replay = call("POST", "/auth/verify", payload, session);
        assertEquals("CHALLENGE_INVALID", json(replay).path("code").asText());
    }

    @Test
    void emailVerificationRequiresCorrectFourDigitPin() throws Exception {
        var session = new MockHttpSession();
        var challenge = json(call("POST", "/auth/login", credentials(), session));
        var code = requestCode(session, challenge, "email");
        var invalid = call("POST", "/auth/verify", verification(challenge, code, "email", "0000"), session);
        assertEquals(400, invalid.getResponse().getStatus());
        assertEquals(401, call("GET", "/accounts", null, session).getResponse().getStatus());
        var valid = call("POST", "/auth/verify", verification(challenge, code, "email", "2468"), session);
        assertEquals(200, valid.getResponse().getStatus());
    }

    @Test
    void duplicateCheckingAndSavingsAreRejectedOnServer() throws Exception {
        var session = signedIn();
        for (String type : new String[]{"checking", "savings"}) {
            var result = call("POST", "/accounts", Map.of("type", type, "name", "Another nickname"), session);
            assertEquals(409, result.getResponse().getStatus());
            assertEquals("ACCOUNT_ALREADY_OPEN", json(result).path("code").asText());
        }
        var products = json(call("GET", "/accounts/products", null, session));
        assertTrue(products.get(0).path("alreadyOpen").asBoolean());
        assertTrue(products.get(1).path("alreadyOpen").asBoolean());
        assertFalse(products.get(3).path("alreadyOpen").asBoolean());
    }

    @Test
    void loanApplicationStaysPendingAndCanBeReadBack() throws Exception {
        var session = signedIn();
        var result = call("POST", "/accounts", Map.of("type", "loan", "name", "Study loan"), session);
        assertEquals(201, result.getResponse().getStatus());
        var account = json(result);
        assertEquals("pending", account.path("status").asText());
        var stored = json(call("GET", "/accounts/" + account.path("id").asText(), null, session));
        assertEquals("Study loan", stored.path("name").asText());
    }

    @Test
    void transactionFilteringAndAssistantUseServerData() throws Exception {
        var session = signedIn();
        var items = json(call("GET", "/transactions?accountId=credit", null, session));
        assertEquals(7, items.size());
        for (var item : items) {
            assertEquals("credit", item.path("accountId").asText());
        }
        var reply = json(call("POST", "/assistant/messages", Map.of("message", "biggest spending category"), session));
        assertTrue(reply.path("reply").asText().contains("$1,929.30"));
        assertTrue(reply.path("reply").asText().contains("Housing"));
        assertEquals("rule-based-demo", reply.path("mode").asText());
    }

    @Test
    void validatesAlertAmountsDatesAndAccountReferences() throws Exception {
        var session = signedIn();
        var invalidAmount = Map.of(
            "type", "low_balance", "accountId", "checking", "title", "Balance",
            "channel", "Email", "amount", -5
        );
        assertEquals(400, call("POST", "/alerts", invalidAmount, session).getResponse().getStatus());
        var invalidDate = Map.of(
            "type", "scheduled", "accountId", "checking", "title", "Bill",
            "channel", "SMS", "date", "2026-02-30"
        );
        assertEquals(400, call("POST", "/alerts", invalidDate, session).getResponse().getStatus());
        var unknownAccount = Map.of(
            "type", "low_balance", "accountId", "missing", "title", "Balance",
            "channel", "Email", "amount", 100
        );
        assertEquals(404, call("POST", "/alerts", unknownAccount, session).getResponse().getStatus());
    }

    @Test
    void alertInboxLifecycleIsStoredOnBackend() throws Exception {
        var session = signedIn();
        var alert = json(call("POST", "/alerts", Map.of(
            "type", "low_balance", "accountId", "checking", "title", "Check balance",
            "channel", "In-app", "amount", 100
        ), session));
        String alertId = alert.path("id").asText();
        call("PATCH", "/alerts/" + alertId, Map.of("enabled", false), session);
        assertEquals(409, call("POST", "/notifications/test", Map.of("alertId", alertId), session)
            .getResponse().getStatus());
        call("PATCH", "/alerts/" + alertId, Map.of("enabled", true), session);
        var notification = json(call("POST", "/notifications/test", Map.of("alertId", alertId), session));
        String id = notification.path("id").asText();
        assertFalse(notification.path("read").asBoolean());
        var resolved = json(call("PATCH", "/notifications/" + id, Map.of("resolved", true), session));
        assertTrue(resolved.path("read").asBoolean());
        assertTrue(resolved.path("resolved").asBoolean());
        assertEquals(204, call("PATCH", "/notifications/read-all", Map.of(), session).getResponse().getStatus());
        assertEquals(204, call("DELETE", "/notifications/" + id, null, session).getResponse().getStatus());
        assertEquals(0, json(call("GET", "/notifications", null, session)).size());
    }

    @Test
    void codeAndSessionExpiryAreEnforcedOnServer() throws Exception {
        var session = new MockHttpSession();
        var challenge = json(call("POST", "/auth/login", credentials(), session));
        var code = requestCode(session, challenge, "sms");
        clock.advance(300_001);
        var expired = call("POST", "/auth/verify", verification(challenge, code, "sms", ""), session);
        assertEquals("CODE_EXPIRED", json(expired).path("code").asText());
        session = signedIn();
        clock.advance(1_800_001);
        assertEquals(401, call("GET", "/accounts", null, session).getResponse().getStatus());
    }

    @Test
    void resendAndFailedVerificationAttemptsAreLimited() throws Exception {
        var session = new MockHttpSession();
        var challenge = json(call("POST", "/auth/login", credentials(), session));
        var code = requestCode(session, challenge, "sms");
        var resend = call("POST", "/auth/code", Map.of(
            "challengeId", challenge.path("challengeId").asText(), "method", "sms"
        ), session);
        assertEquals(429, resend.getResponse().getStatus());
        String wrongCode = code.path("demoCode").asText().equals("000000") ? "111111" : "000000";
        var invalid = Map.of(
            "challengeId", challenge.path("challengeId").asText(), "method", "sms", "code", wrongCode
        );
        for (int attempt = 0; attempt < 5; attempt++) {
            call("POST", "/auth/verify", invalid, session);
        }
        var locked = call("POST", "/auth/verify", verification(challenge, code, "sms", ""), session);
        assertEquals("ATTEMPTS_EXCEEDED", json(locked).path("code").asText());
    }

    @Test
    void logoutInvalidatesSessionAndUiAssetsAreServed() throws Exception {
        var session = signedIn();
        assertEquals(204, call("POST", "/auth/logout", Map.of(), session).getResponse().getStatus());
        assertTrue(session.isInvalid());
        assertEquals(401, call("GET", "/accounts", null, new MockHttpSession()).getResponse().getStatus());
        var page = mvc.perform(MockMvcRequestBuilders.get("/login.html")).andReturn();
        assertEquals(200, page.getResponse().getStatus());
        assertTrue(page.getResponse().getContentAsString().contains("auth/login-page.js"));
    }

    private MockHttpSession signedIn() throws Exception {
        var session = new MockHttpSession();
        var challenge = json(call("POST", "/auth/login", credentials(), session));
        var code = requestCode(session, challenge, "sms");
        var result = call("POST", "/auth/verify", verification(challenge, code, "sms", ""), session);
        assertEquals(200, result.getResponse().getStatus());
        return session;
    }

    private Map<String, String> credentials() {
        return Map.of("username", "chengyang.lee", "password", "HikyuDemo2026!");
    }

    private JsonNode requestCode(MockHttpSession session, JsonNode challenge, String method) throws Exception {
        return json(call("POST", "/auth/code", Map.of(
            "challengeId", challenge.path("challengeId").asText(), "method", method
        ), session));
    }

    private Map<String, String> verification(JsonNode challenge, JsonNode code, String method, String pin) {
        return Map.of(
            "challengeId", challenge.path("challengeId").asText(),
            "method", method, "code", code.path("demoCode").asText(), "pin", pin
        );
    }

    private MvcResult call(String method, String path, Object body, MockHttpSession session) throws Exception {
        var request = MockMvcRequestBuilders.request(
            org.springframework.http.HttpMethod.valueOf(method), "/api/v1" + path
        ).header("X-Hikyu-Request", "web").contentType(MediaType.APPLICATION_JSON);
        if (session != null) {
            request.session(session);
        }
        if (body != null) {
            request.content(mapper.writeValueAsString(body));
        }
        return mvc.perform(request).andReturn();
    }

    private JsonNode json(MvcResult result) throws Exception {
        return mapper.readTree(result.getResponse().getContentAsString());
    }

    @TestConfiguration
    static class ClockConfiguration {
        @Bean
        @Primary
        MutableClock testClock() {
            return new MutableClock();
        }
    }

    static final class MutableClock extends Clock {
        private final AtomicLong now = new AtomicLong();

        void reset() {
            now.set(Instant.parse("2026-09-29T12:00:00Z").toEpochMilli());
        }

        void advance(long milliseconds) {
            now.addAndGet(milliseconds);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(now.get());
        }
    }
}
