package caixa.psi.service;

import caixa.psi.dto.RequestLoanDataDTO;
import caixa.psi.dto.ResponseLoanDataDTO;
import caixa.psi.entity.Product;
import caixa.psi.model.CalculationMemoryUnity;
import caixa.psi.repository.ProductDAO;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class LoanServiceTest {

    private ProductDAO productDAO;
    private ProductService productService;
    private LoanService loanService;

    @BeforeEach
    void setup() {
        this.productDAO = mock(ProductDAO.class);
        this.productService = mock(ProductService.class);
        this.loanService = new LoanService(productService);
    }

    @Test
    void shouldThrows409WhenMaxInstallmentsIsInvalid() {

        Product fakeProduct = new Product("Financiamento Habitacional", new BigDecimal("7.4"), (short) 5);

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productService.checkIfProductExists(Mockito.any(UUID.class))).thenReturn(fakeProduct);

        RequestLoanDataDTO dto = new RequestLoanDataDTO(
            UUID.randomUUID(),
            new BigDecimal("4000.00"),
            40
        );

        Response response = loanService.getLoanData(dto);

        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
            Assertions.assertTrue(response.getEntity()
                .toString()
                .contains("Número de parcelas requerido é menor que o permitido pelo produto."));
        });
    }

    @Test
    void shouldCreateLoanDataSuccessfully() {

        Product fakeProduct = new Product(UUID.randomUUID(), "Financiamento Habitacional", new BigDecimal("7.4"), (short) 420);

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productService.checkIfProductExists(Mockito.any(UUID.class))).thenReturn(fakeProduct);

        RequestLoanDataDTO requestDTO = new RequestLoanDataDTO(
                UUID.randomUUID(),
                new BigDecimal("50000.00"),
                96
        );

        List<CalculationMemoryUnity> calculationMemoryUnityList = null;
        ResponseLoanDataDTO expectedDTOResponse = new ResponseLoanDataDTO(
                fakeProduct,
                new BigDecimal("50000.00"),
                (short) 96,
                new BigDecimal("0.005967"),
                new BigDecimal("66401.42"),
                new BigDecimal("691.68"),
                calculationMemoryUnityList
        );

        Assertions.assertDoesNotThrow(() -> {
            Response response = loanService.getLoanData(requestDTO);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

            ResponseLoanDataDTO responseDTO = (ResponseLoanDataDTO) response.getEntity();

            Assertions.assertEquals(expectedDTOResponse.monthlyInstallment(), responseDTO.monthlyInstallment());
            Assertions.assertEquals(expectedDTOResponse.totalLoanValue(), responseDTO.totalLoanValue());
            Assertions.assertEquals(expectedDTOResponse.product(), responseDTO.product());
            Assertions.assertEquals(expectedDTOResponse.requestedInstallment(), responseDTO.requestedInstallment());
            Assertions.assertEquals(expectedDTOResponse.requestedSum(), responseDTO.requestedSum());
            Assertions.assertEquals(expectedDTOResponse.effectiveMonthlyInterestRate(), responseDTO.effectiveMonthlyInterestRate());
            Assertions.assertFalse(responseDTO.calculationMemory().isEmpty());
            Assertions.assertEquals(96, responseDTO.calculationMemory().size());
        });

    }

}
