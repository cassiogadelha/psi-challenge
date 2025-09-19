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

        Product product = productService.getProduct(dto.productId());

        BigDecimal annualInterestRate = product.getAnnualInterestRate();
        BigDecimal monthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(100 * 12), 3, RoundingMode.HALF_UP);

        BigDecimal installment = generateInstallment(monthlyInterestRate, dto);
        BigDecimal totalLoanValue = installment.multiply(BigDecimal.valueOf(dto.requestedInstallments()));

        ResponseLoanDataDTO loanDataDTO = new ResponseLoanDataDTO(
            product,
            dto.requestedSum().setScale(2, RoundingMode.HALF_UP),
            dto.requestedInstallments(),
            generateEffectiveMonthlyInterestRate(annualInterestRate),
            totalLoanValue.setScale(2, RoundingMode.HALF_UP),
            installment.setScale(2, RoundingMode.HALF_UP),
            generateCalculationMemory(monthlyInterestRate, dto, installment)
        );

        return Response.ok(loanDataDTO).build();
    }

    private BigDecimal generateInstallment(BigDecimal monthlyInterestRate, RequestLoanDataDTO dto) {

        BigDecimal compoundFactor = BigDecimal.valueOf(Math.pow((1.0 + monthlyInterestRate.doubleValue()), dto.requestedInstallments()));
        BigDecimal numerator = monthlyInterestRate.multiply(compoundFactor);
        BigDecimal denominator = compoundFactor.subtract(BigDecimal.ONE);
        return dto.requestedSum().multiply(numerator.divide(denominator, RoundingMode.HALF_UP));

    }

    private List<CalculationMemoryUnity> generateCalculationMemory(BigDecimal monthlyInterestRate, RequestLoanDataDTO dto, BigDecimal installment){

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

    private BigDecimal generateEffectiveMonthlyInterestRate(BigDecimal annualInterestRate) {

        BigDecimal effectiveMonthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
                .add(BigDecimal.valueOf(1));

        return BigDecimal.valueOf((Math.pow(effectiveMonthlyInterestRate.doubleValue(), (1.0 / 12.0))) - 1);

    }
}
