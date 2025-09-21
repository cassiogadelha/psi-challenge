package caixa.psi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductDTO(

        @NotNull(message = "Informe um nome para o produto.")
        @NotBlank(message = "Informe um nome válido para o produto.")
        String name,

        @Positive(message = "Informe um valor positivo.")
        @NotNull(message = "Informe a  taxa de juros do produto.")
        BigDecimal annualInterestRate,

        @NotNull(message = "Informe a quantidade máxima de parcelas que o produto pode ter.")
        @Positive(message = "Informe um valor positivo.")
        short maxInstallments

) {
}
