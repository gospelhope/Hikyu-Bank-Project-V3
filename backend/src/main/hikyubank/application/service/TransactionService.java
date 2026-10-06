package hikyubank.application.service;

import hikyubank.application.model.Transaction;
import hikyubank.dataaccess.repository.TransactionRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactions;
    private final AccountService accounts;

    public TransactionService(TransactionRepository transactions, AccountService accounts) {
        this.transactions = transactions;
        this.accounts = accounts;
    }

    public List<Transaction> list(String accountId) {
        if (accountId != null && !accountId.isBlank()) {
            accounts.get(accountId);
        }
        return transactions.findAll().stream()
            .filter(item -> accountId == null || accountId.isBlank()
                || item.accountId().equals(accountId))
            .sorted(Comparator.comparing(Transaction::date).reversed())
            .toList();
    }
}
