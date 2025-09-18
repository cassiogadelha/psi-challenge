package caixa.psi.dto;

import caixa.psi.entity.Product;
import jakarta.validation.constraints.NotNull;

public record ResponseLoanDataDTO(

        Product product,
        float requestedSum,
        int requestedInstallment,
        float effectiveMonthlyInterestRate,
        float totalLoanValue,
        float monthlyInstallment


) {
}
