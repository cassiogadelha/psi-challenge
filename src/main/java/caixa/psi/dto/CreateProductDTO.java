package caixa.psi.dto;


import jakarta.validation.constraints.NotNull;

public record CreateProductDTO(
        @NotNull(message = "Informe um nome para o produto.")
        String name,

        @NotNull(message = "Informe a  taxa de juros do produto.")
        float annualInterestRate,

        @NotNull(message = "Informe a quantidade máxima de parcelas que o produto pode ter.")
        short maxInstallments
) {
}
