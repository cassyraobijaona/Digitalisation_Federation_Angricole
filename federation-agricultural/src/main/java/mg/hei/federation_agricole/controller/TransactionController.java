package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.exception.BadRequestException;
import mg.hei.federation_agricole.model.dto.CollectivityTransaction;
import mg.hei.federation_agricole.service.TransactionService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> get(@PathVariable String id,
                                 @RequestParam LocalDate from,
                                 @RequestParam LocalDate to) {

        if (from == null || to == null)
            throw new BadRequestException("Parameters 'from' and 'to' are required");

        if (from.isAfter(to))
            throw new BadRequestException("'from' must be before 'to'");

        return ResponseEntity.ok(service.getTransactions(id, from, to)); // 200
    }
}
