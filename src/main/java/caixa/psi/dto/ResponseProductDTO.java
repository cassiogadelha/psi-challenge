package caixa.psi.dto;

import java.util.UUID;

public record ResponseProductDTO(
    UUID id,
    String name,
    float annualInterestRate,
    int maxInstallments
) {
}
