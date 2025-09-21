package caixa.psi.utils;

import caixa.psi.dto.RequestLoanDataDTO;
import caixa.psi.model.CalculationMemoryUnity;
import io.quarkus.logging.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CalculationMemoryGenerator {

    public static List<CalculationMemoryUnity> generateCalculationMemory(

            BigDecimal monthlyInterestRate,
            RequestLoanDataDTO dto,
            BigDecimal installment

    ){

        BigDecimal initialLoanBalance = dto.requestedSum();
        BigDecimal finalLoanBalance;

        List<CalculationMemoryUnity> calculationMemory = new ArrayList<>();

        BigDecimal interest = initialLoanBalance.multiply(monthlyInterestRate);

        BigDecimal amortization = installment.subtract(interest);

        for (int i = 1; i <= dto.requestedInstallments(); i ++) {

            finalLoanBalance = initialLoanBalance.subtract(amortization);

            if (i == dto.requestedInstallments() && finalLoanBalance.compareTo(BigDecimal.valueOf(0.01)) < 0) {
                finalLoanBalance = BigDecimal.ZERO;
            }


            CalculationMemoryUnity unity = new CalculationMemoryUnity(
                    i,
                    initialLoanBalance.setScale(2, RoundingMode.HALF_UP),
                    interest.setScale(2, RoundingMode.HALF_UP),
                    amortization.setScale(2, RoundingMode.HALF_UP),
                    finalLoanBalance.setScale(2, RoundingMode.HALF_UP)
            );

            calculationMemory.add(unity);

            initialLoanBalance = finalLoanBalance;
            interest = initialLoanBalance.multiply(monthlyInterestRate);
            amortization = installment.subtract(interest);
        }

        return calculationMemory;
    }
}
