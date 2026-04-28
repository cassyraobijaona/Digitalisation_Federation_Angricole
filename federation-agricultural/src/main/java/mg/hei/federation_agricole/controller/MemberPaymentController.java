package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.model.dto.MemberPayment;
import mg.hei.federation_agricole.service.CollectivityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberPaymentController {

    private final CollectivityService service;

    public MemberPaymentController(CollectivityService service) {
        this.service = service;
    }

    @PostMapping("/{id}/payments")
    public void pay(@PathVariable String id, @RequestBody MemberPayment p) {
        p.setMemberId(id);
        service.pay(p);
    }
}

