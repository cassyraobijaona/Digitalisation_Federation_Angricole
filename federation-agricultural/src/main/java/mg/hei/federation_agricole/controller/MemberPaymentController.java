package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.exception.BadRequestException;
import mg.hei.federation_agricole.model.dto.MemberPayment;
import mg.hei.federation_agricole.service.CollectivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberPaymentController {

    private final CollectivityService service;

    public MemberPaymentController(CollectivityService service) {
        this.service = service;
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<?> pay(@PathVariable String id,
                                 @RequestBody List<MemberPayment> payments) {

        if (payments == null || payments.isEmpty())
            throw new BadRequestException("Payments list cannot be empty");

        for (MemberPayment p : payments) {
            if (p.getAmount() == null || p.getAmount() <= 0)
                throw new BadRequestException("Amount must be > 0");
            if (p.getPaymentMode() == null)
                throw new BadRequestException("Payment mode is required");
            if (p.getMembershipFeeIdentifier() == null)
                throw new BadRequestException("membershipFeeIdentifier is required");
            if (p.getAccountCreditedIdentifier() == null)
                throw new BadRequestException("accountCreditedIdentifier is required");

            p.setMemberId(id);
            service.pay(p);
        }

        return ResponseEntity.status(201).build(); // 201
    }
}

