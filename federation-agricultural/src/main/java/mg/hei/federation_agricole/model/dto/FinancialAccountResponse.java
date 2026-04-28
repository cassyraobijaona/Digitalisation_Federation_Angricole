package mg.hei.federation_agricole.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FinancialAccountResponse {
    private String id;
    private String accountType;
    private Double balance;
}
