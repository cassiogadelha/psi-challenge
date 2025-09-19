package caixa.psi.dto;

import caixa.psi.entity.Product;
import caixa.psi.model.CalculationMemoryUnity;

import java.math.BigDecimal;
import java.util.List;

public record ResponseLoanDataDTO(

        Product product,
        BigDecimal requestedSum,
        int requestedInstallment,
        BigDecimal effectiveMonthlyInterestRate,
        BigDecimal totalLoanValue,
        BigDecimal monthlyInstallment,
        List<CalculationMemoryUnity> calculationMemory

) {
}
