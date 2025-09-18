package caixa.psi.service;

import caixa.psi.dto.RequestLoanDataDTO;
import caixa.psi.dto.ResponseLoanDataDTO;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@ApplicationScoped
public class LoanService {

    private final ProductService productService;

    public LoanService(ProductService productService) {
        this.productService = productService;
    }

    public Response getLoanData(RequestLoanDataDTO dto) {

        if (!checkMaxInstallment(dto.productId(), dto.requestedInstallments())) return Response.status(409).build();

        createLoanData(dto);

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

    private ResponseLoanDataDTO createLoanData(RequestLoanDataDTO dto) {



        float annualInterestRate = productService.getProduct(dto.productId()).getAnnualInterestRate();
        float monthlyInterestRate = (annualInterestRate / 100 / 12);

        float compoundFactor = (float) Math.pow((1 + monthlyInterestRate), dto.requestedInstallments());

        float effectiveMonthlyInterestRate = (float) (Math.pow((monthlyInterestRate + 1), 12)) - 1;

        float installment = dto.requestedSum() * ((monthlyInterestRate * compoundFactor) / (compoundFactor - 1));

        for (int i = 1; i <= dto.requestedInstallments(); i ++) {

        }
    }
}
