package caixa.psi.dto;

import java.math.BigDecimal;

public record PatchProductDTO(

        String name,
        BigDecimal annualInterestRate,
        short maxInstallments

) {
}
