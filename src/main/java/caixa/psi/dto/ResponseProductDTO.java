package caixa.psi.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseProductDTO(
    UUID id,
    String name,
    BigDecimal annualInterestRate,
    int maxInstallments
) {
}
