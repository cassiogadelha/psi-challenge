package caixa.psi.utils;

import caixa.psi.dto.RequestLoanDataDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class InstallmentGeneratorTest {

    @Test
    void shouldGenerateInstallmentSuccessfully() {

        RequestLoanDataDTO dto = new RequestLoanDataDTO(
                UUID.randomUUID(),
                BigDecimal.valueOf(7000),
                37
        );

        BigDecimal expectedInstallment = BigDecimal.valueOf(269.55);

        BigDecimal result = InstallmentGenerator.installmentCalculator(BigDecimal.valueOf(0.02), dto);

        Assertions.assertEquals(expectedInstallment, result.setScale(2, RoundingMode.HALF_DOWN));

    }
}
