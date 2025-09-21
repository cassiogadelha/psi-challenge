package caixa.psi.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EffectiveMonthlyRateCalculatorTest {

    @Test
    void shouldGenerateEffectiveMonthlyInterestRateSuccessfully() {

        BigDecimal annualInterestRate = BigDecimal.valueOf(18.0);
        BigDecimal effectiveMonthlyRateExpected = BigDecimal.valueOf(0.013888);

        BigDecimal result = EffectiveMonthlyRateCalculator.calculateEffectiveMonthlyRate(annualInterestRate);

        Assertions.assertEquals(effectiveMonthlyRateExpected, result.setScale(6, RoundingMode.HALF_UP));

    }
}
