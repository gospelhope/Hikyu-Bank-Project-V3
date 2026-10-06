package hikyubank.web.controller;

import hikyubank.application.model.Transaction;
import hikyubank.application.service.TransactionService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService transactions;

    public TransactionController(TransactionService transactions) {
        this.transactions = transactions;
    }

    @GetMapping
    public List<Transaction> list(@RequestParam(required = false) String accountId) {
        return transactions.list(accountId);
    }
}
