package caixa.psi.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestLoanDataDTO(
        @NotNull(message = "Informe o ID do produto.")
        UUID productId,

        @NotNull(message = "Informe o valor desejado.")
        BigDecimal requestedSum,

        @NotNull(message = "Informe o prazo desejado.")
        int requestedInstallments
) {
}
