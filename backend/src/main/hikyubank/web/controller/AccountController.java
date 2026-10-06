package hikyubank.web.controller;

import hikyubank.application.dto.BankDtos.OpenAccountRequest;
import hikyubank.application.dto.BankDtos.AccountProduct;
import hikyubank.application.model.Account;
import hikyubank.application.service.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accounts;

    public AccountController(AccountService accounts) {
        this.accounts = accounts;
    }

    @GetMapping
    public List<Account> list() {
        return accounts.list();
    }

    @GetMapping("/products")
    public List<AccountProduct> products() {
        return accounts.products();
    }

    @GetMapping("/{id}")
    public Account get(@PathVariable String id) {
        return accounts.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account open(@Valid @RequestBody OpenAccountRequest input) {
        return accounts.open(input);
    }
}
