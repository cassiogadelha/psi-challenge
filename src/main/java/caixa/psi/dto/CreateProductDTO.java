package caixa.psi.dto;


import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductDTO(

        @NotNull(message = "Informe um nome para o produto.")
        String name,

        @NotNull(message = "Informe a  taxa de juros do produto.")
        BigDecimal annualInterestRate,

        @NotNull(message = "Informe a quantidade máxima de parcelas que o produto pode ter.")
        short maxInstallments
) {
}
