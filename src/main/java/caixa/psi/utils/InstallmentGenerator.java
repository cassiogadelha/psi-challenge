package caixa.psi.utils;

import caixa.psi.dto.RequestLoanDataDTO;
import io.quarkus.logging.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InstallmentGenerator {

    public static BigDecimal installmentCalculator(BigDecimal monthlyInterestRate, RequestLoanDataDTO dto) {

        BigDecimal compoundFactor = BigDecimal.valueOf(
                Math.pow((1.0 + monthlyInterestRate.doubleValue()),
                dto.requestedInstallments())
        );

        BigDecimal numerator = monthlyInterestRate.multiply(compoundFactor);
        BigDecimal denominator = compoundFactor.subtract(BigDecimal.ONE);

        return dto.requestedSum().multiply(numerator.divide(denominator, RoundingMode.HALF_UP));
    }
}
