package caixa.psi.resource;

import caixa.psi.dto.RequestLoanDataDTO;
import caixa.psi.service.LoanService;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/loan")
public class LoanResource {

    LoanService loanService;

    public LoanResource(LoanService loanService) {
        this.loanService = loanService;
    }

    @POST
    public Response createLoanData(RequestLoanDataDTO dto) {
        return loanService.getLoanData(dto);
    }

}
