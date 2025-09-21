package caixa.psi.resource;

import caixa.psi.dto.RequestLoanDataDTO;
import caixa.psi.dto.ResponseLoanDataDTO;
import caixa.psi.service.LoanService;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/v1/loan")
public class LoanResource {

    LoanService loanService;

    public LoanResource(LoanService loanService) {
        this.loanService = loanService;
    }

    @POST
    @Operation(summary = "Cria dados referentes a uma simulação de empréstimo.")
    @APIResponse(responseCode = "200", description = "Simulação gerada com sucesso.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ResponseLoanDataDTO.class)))
    @APIResponse(responseCode = "404", description = "Produto não encontrado.")
    @APIResponse(responseCode = "409", description = "Número de parcelas requerido é menor que o permitido pelo produto.")
    public Response createLoanData(
            @RequestBody(description = "DTO com dados válidos de um pedido de empréstimo que gerará uma simulação.",
                    content = @Content(schema = @Schema(implementation = RequestLoanDataDTO.class)))
            RequestLoanDataDTO dto) {
        return loanService.getLoanData(dto);
    }

}
