package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.model.dto.FinancialAccount;
import mg.hei.federation_agricole.model.dto.FinancialAccountEntity;
import mg.hei.federation_agricole.service.FinancialAccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class FinancialAccountController {

    private final FinancialAccountService service;

    public FinancialAccountController(FinancialAccountService service) {
        this.service = service;
    }

    @PostMapping
    public FinancialAccount create(@RequestBody FinancialAccountEntity account) {
        return service.createAccount(account);
    }

    @GetMapping("/{id}")
    public FinancialAccount get(@PathVariable Integer id) {
        return service.getAccount(id);
    }
}