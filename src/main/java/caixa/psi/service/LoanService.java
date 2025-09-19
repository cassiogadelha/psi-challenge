package caixa.psi.service;

import caixa.psi.dto.RequestLoanDataDTO;
import caixa.psi.dto.ResponseLoanDataDTO;
import caixa.psi.entity.Product;
import caixa.psi.model.CalculationMemoryUnity;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LoanService {

    private final ProductService productService;

    public LoanService(ProductService productService) {
        this.productService = productService;
    }

    public Response getLoanData(RequestLoanDataDTO dto) {

        if (!checkMaxInstallment(dto.productId(), dto.requestedInstallments())) return Response.status(409).build();

        return createLoanData(dto);

    }

    private boolean checkMaxInstallment(UUID productId, int requestedProductInstallment) {

        int productMaxInstallment;

        try {
            productMaxInstallment = productService.getProduct(productId).getMaxInstallments();

            return requestedProductInstallment <= productMaxInstallment;
        } catch (Exception e) {
            Log.info(e);
        }

        return false;
    }

    private Response createLoanData(RequestLoanDataDTO dto) {

        BigDecimal annualInterestRate = productService.getProduct(dto.productId()).getAnnualInterestRate();
        Log.info("ANNUAL INTEREST RATE: " + annualInterestRate);
        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(100 * 12), 2, RoundingMode.HALF_UP);
        float monthlyInterestRate = monthlyRate.floatValue();

        Log.info("MONTHLY RATE: " + monthlyRate);

        BigDecimal compoundFactor = BigDecimal.valueOf(Math.pow((1 + monthlyInterestRate), dto.requestedInstallments()));

        BigDecimal initialLoanBalance = dto.requestedSum();
        BigDecimal finalLoanBalance;

        BigDecimal effectiveMonthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
                .add(BigDecimal.valueOf(1));
        effectiveMonthlyInterestRate = BigDecimal.valueOf((Math.pow(effectiveMonthlyInterestRate.doubleValue(), (1.0 / 12.0))) - 1);
        Log.info("EFFECTIVE MONTHLY INTEREST RATE: " + effectiveMonthlyInterestRate);


        Log.info("COMPOUND: " + compoundFactor);
        BigDecimal numerator = monthlyRate.multiply(compoundFactor);
        Log.info("NUMERATOR: " + numerator);
        BigDecimal denominator = compoundFactor.subtract(BigDecimal.ONE);
        Log.info("DENOMINATOR: " + denominator);
        BigDecimal installment = dto.requestedSum().multiply(numerator.divide(denominator, RoundingMode.HALF_UP));
        Log.info("INSTALLMENT: " + installment);

        BigDecimal totalLoanValue = installment.multiply(BigDecimal.valueOf(dto.requestedInstallments()));

        List<CalculationMemoryUnity> calculationMemory = new ArrayList<>();

        BigDecimal interest = initialLoanBalance.multiply(BigDecimal.valueOf(monthlyInterestRate));
        Log.info("INTEREST: " + interest);
        BigDecimal amortization = installment.subtract(interest);
        Log.info("AMORTIZATION: " + amortization);
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
            interest = initialLoanBalance.multiply(BigDecimal.valueOf(monthlyInterestRate));
            amortization = installment.subtract(interest);
        }

        Product product = productService.getProduct(dto.productId());
        ResponseLoanDataDTO loanDataDTO = new ResponseLoanDataDTO(
            product,
            dto.requestedSum().setScale(2, RoundingMode.HALF_UP),
            dto.requestedInstallments(),
            effectiveMonthlyInterestRate,
            totalLoanValue.setScale(2, RoundingMode.HALF_UP),
            installment.setScale(2, RoundingMode.HALF_UP),
            calculationMemory
        );

        return Response.ok(loanDataDTO).build();
    }
}
