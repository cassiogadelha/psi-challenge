package caixa.psi.resource;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.PatchProductDTO;
import caixa.psi.dto.ResponseProductDTO;
import caixa.psi.dto.UpdateProductDTO;
import caixa.psi.entity.Product;
import caixa.psi.service.ProductService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/api/v1/product")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService){
        this.productService = productService;
    }

    @GET
    @Operation(summary = "Lista todos os produtos cadastrados.")
    @APIResponse(responseCode = "200", description = "Lista de produtos.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ResponseProductDTO.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "200", description = "Retorna mensagem caso nenhuma produto seja encontrado.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Map.class)))
    public Response getAllProducts(@QueryParam("page") @DefaultValue("0") int page,
                                   @QueryParam("size") @DefaultValue("10") int size){

        return productService.getAllProducts(page, size);
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Busca produto cadastrado por ID.")
    @APIResponse(responseCode = "200", description = "Produto encontrado.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ResponseProductDTO.class)))
    @APIResponse(responseCode = "404", description = "Produto não encontrado.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Map.class)))
    public Response findById(@PathParam("id") UUID id) {

        return productService.findById(id);

    }

    @POST
    @Operation(summary = "Cria um novo produto com campos válidos.")
    @APIResponse(responseCode = "201", description = "Produto criado.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ResponseProductDTO.class)))
    public Response createProduct(
            @RequestBody(description = "DTO com dados válidos de um produto a ser criado.",
                content = @Content(schema = @Schema(implementation = CreateProductDTO.class)))
            @Valid CreateProductDTO dto) {

        return productService.createProduct(dto);

    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Deleta veículo a partir de um ID")
    @APIResponse(responseCode = "204", description = "Produto deletado.")
    @APIResponse(responseCode = "404", description = "Produto não encontrado.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Map.class)))
    public Response deleteProduct(
            @Parameter(description = "ID do produto que será excluído", required = true)
            @PathParam("id") UUID id){

        return productService.deleteProduct(id);

    }

    @PUT
    @Path("{id}")
    @Operation(summary = "Atualiza completamente um produto a partir de um ID e JSON")
    @APIResponse(responseCode = "200", description = "Produto atualizado.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ResponseProductDTO.class)))
    @APIResponse(responseCode = "404", description = "Produto não encontrado.")
    public Response updateProduct(
            @Parameter(description = "ID do produto que será atualizado.", required = true)
            @PathParam("id") UUID id,
            @RequestBody(description = "DTO com dados válidos que atualizará todos os atributos do produto.",
                content = @Content(schema = @Schema(implementation = UpdateProductDTO.class)))
            @Valid UpdateProductDTO dto) {

        return productService.updateProduct(id, dto);

    }

    @PATCH
    @Path("{id}")
    @Operation(summary = "Atualiza parcialmente um produto a partir de um ID e JSON. É possível atualizar totalmente também.")
    @APIResponse(responseCode = "200", description = "Produto atualizado parcialmente.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ResponseProductDTO.class)))
    @APIResponse(responseCode = "404", description = "Produto não encontrado.",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Map.class)))
    public Response patchProduct(
            @Parameter(description = "ID do produto que será atualizado parcialmente ou totalmente.", required = true)
            @PathParam("id") UUID id,
            @RequestBody(description = "DTO com dados válidos que atualizará alguns ou todos atributos do produto.",
                content = @Content(schema = @Schema(implementation = PatchProductDTO.class)))
            PatchProductDTO dto) {

        return productService.patchProduct(id, dto);

    }
}
