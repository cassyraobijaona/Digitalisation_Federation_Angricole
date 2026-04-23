package mg.hei.federation_agricole.model.dto;

import lombok.Data;
import mg.hei.federation_agricole.model.enums.PaymentMode;

import java.time.LocalDate;

@Data
public class CollectivityTransaction {
    private Integer id;
    private LocalDate creationDate;
    private double amount;
    private PaymentMode paymentMode;
    private Member memberDebited;              // 🔥 AJOUT
    private FinancialAccount accountCredited;
}
