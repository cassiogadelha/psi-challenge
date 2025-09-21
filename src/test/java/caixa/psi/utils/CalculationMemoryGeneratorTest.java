package caixa.psi.utils;

import caixa.psi.dto.RequestLoanDataDTO;
import static caixa.psi.utils.CalculationMemoryGenerator.generateCalculationMemory;
import caixa.psi.model.CalculationMemoryUnity;
import io.quarkus.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class CalculationMemoryGeneratorTest {

    @Test
    void shouldGenerateCalculationMemorySuccessfully() {

        RequestLoanDataDTO dto = new RequestLoanDataDTO(
                UUID.randomUUID(),
                BigDecimal.valueOf(7000),
                37
        );

        BigDecimal installment = InstallmentGenerator.installmentCalculator(BigDecimal.valueOf(0.02), dto);
        List<CalculationMemoryUnity> calculationMemoryList = generateCalculationMemory(
                BigDecimal.valueOf(0.02),
                dto,
                installment);

        CalculationMemoryUnity first = calculationMemoryList.stream().findFirst().get();
        CalculationMemoryUnity lastOne = calculationMemoryList.getLast();

        Assertions.assertEquals(dto.requestedInstallments(), calculationMemoryList.stream().toList().size());
        Assertions.assertEquals(0, first.getInterest().compareTo(BigDecimal.valueOf(140.00)));
        Assertions.assertEquals(BigDecimal.valueOf(269.55).subtract(first.getInterest()), first.getAmortization());
        Assertions.assertEquals(1, first.getMonth());
        Assertions.assertEquals(BigDecimal.valueOf(7000).setScale(2), first.getInitialLoanBalance());
        Assertions.assertEquals(BigDecimal.valueOf(6870.45), first.getInitialLoanBalance().subtract(first.getAmortization()));

        Assertions.assertEquals(new BigDecimal("0.00"), lastOne.getFinalLoanBalance());

    }

}
