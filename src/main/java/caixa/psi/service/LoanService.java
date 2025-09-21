package caixa.psi.service;

import caixa.psi.dto.RequestLoanDataDTO;
import caixa.psi.dto.ResponseLoanDataDTO;
import caixa.psi.entity.Product;
import caixa.psi.utils.InstallmentGenerator;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

import static caixa.psi.utils.CalculationMemoryGenerator.generateCalculationMemory;
import static caixa.psi.utils.EffectiveMonthlyRateCalculator.calculateEffectiveMonthlyRate;

@ApplicationScoped
public class LoanService {

    private final ProductService productService;

    public LoanService(ProductService productService) {
        this.productService = productService;
    }

    public Response getLoanData(RequestLoanDataDTO dto) {

        if (!checkMaxInstallment(dto.productId(), dto.requestedInstallments())) {

            Map<String, String> message = Map.of("mensagem", "Número de parcelas requerido é menor que o permitido pelo produto.");

            return Response.status(Response.Status.CONFLICT).entity(message).build();
        }

        return Response.ok(createLoanData(dto)).build();

    }

    private ResponseLoanDataDTO createLoanData(RequestLoanDataDTO dto) {

        Product product = productService.checkIfProductExists(dto.productId());

        BigDecimal annualInterestRate = product.getAnnualInterestRate();
        BigDecimal monthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(100 * 12), 6, RoundingMode.HALF_UP);

        BigDecimal installment = InstallmentGenerator.installmentCalculator(monthlyInterestRate, dto)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalLoanValue = installment.multiply(BigDecimal.valueOf(dto.requestedInstallments()));


        return new ResponseLoanDataDTO(
            product,
            dto.requestedSum().setScale(2, RoundingMode.HALF_UP),
            dto.requestedInstallments(),
            calculateEffectiveMonthlyRate(annualInterestRate).setScale(6, RoundingMode.HALF_UP),
            totalLoanValue.setScale(2, RoundingMode.HALF_UP),
            installment.setScale(2, RoundingMode.HALF_UP),
            generateCalculationMemory(monthlyInterestRate, dto, installment)
        );

    }

    private boolean checkMaxInstallment(UUID productId, int requestedProductInstallment) {

        int productMaxInstallment;

        try {
            productMaxInstallment = productService.checkIfProductExists(productId).getMaxInstallments();

            return requestedProductInstallment <= productMaxInstallment;
        } catch (Exception e) {
            Log.info(e);
        }

        return false;
    }
}
