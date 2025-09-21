package caixa.psi.utils;

import io.quarkus.logging.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EffectiveMonthlyRateCalculator {

    public static BigDecimal calculateEffectiveMonthlyRate(BigDecimal annualInterestRate) {

        BigDecimal effectiveAnnualInterestRate = annualInterestRate.divide(BigDecimal.valueOf(100.0), 4, RoundingMode.HALF_UP);

        double convertedInterestRate = effectiveAnnualInterestRate.add(BigDecimal.valueOf(1.0)).doubleValue();

        return BigDecimal.valueOf(Math.pow(convertedInterestRate, 1.0 / 12.0) - 1);
    }

}
