package caixa.psi.resource;

import caixa.psi.dto.RequestLoanDataDTO;
import caixa.psi.dto.ResponseLoanDataDTO;
import caixa.psi.entity.Product;
import caixa.psi.model.CalculationMemoryUnity;
import caixa.psi.service.LoanService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class LoanResourceTest {

    private final LoanService loanService = Mockito.mock(LoanService.class);
    private LoanResource loanResource = new LoanResource(loanService);

    @Test
    void shouldCreateLoanDateSuccessfully() {

        Product fakeProduct = new Product("Pronaf", new BigDecimal("4.0"), (short) 36);
        List<CalculationMemoryUnity> fakeCalcultionMemory = null;

        ResponseLoanDataDTO fakeDTO = new ResponseLoanDataDTO(
            fakeProduct,
            new BigDecimal("5600.0"),
            24,
            new BigDecimal("0.004567"),
            new BigDecimal("5890.45"),
            new BigDecimal("287.10"),
            fakeCalcultionMemory
        );

        Response fakeResponse = Response.ok(fakeDTO).build();

        Mockito.when(loanService.getLoanData(Mockito.any(RequestLoanDataDTO.class))).thenReturn(fakeResponse);

        RequestLoanDataDTO fakeRequestDTO = new RequestLoanDataDTO(
                UUID.randomUUID(),
                new BigDecimal("56000.0"),
                24);

        Assertions.assertDoesNotThrow(() -> {
            Response response = loanResource.createLoanData(fakeRequestDTO);

            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeDTO, response.getEntity());
        });

    }
}
