package hikyubank.application.service;

import hikyubank.application.dto.BankDtos.AssistantResponse;
import hikyubank.application.integration.AssistantGateway;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {
    private final AssistantGateway gateway;
    private final AccountService accounts;
    private final TransactionService transactions;

    public AssistantService(
        AssistantGateway gateway,
        AccountService accounts,
        TransactionService transactions
    ) {
        this.gateway = gateway;
        this.accounts = accounts;
        this.transactions = transactions;
    }

    public AssistantResponse ask(String message) {
        return new AssistantResponse(
            gateway.reply(message, accounts.list(), transactions.list(null)),
            "rule-based-demo"
        );
    }
}
