package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.model.dto.CollectivityTransaction;
import mg.hei.federation_agricole.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/{id}/transactions")
    public List<CollectivityTransaction> get(
            @PathVariable Integer id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return service.getTransactions(id, from, to);
    }
}
