package caixa.psi.resource;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.PatchProductDTO;
import caixa.psi.dto.UpdateProductDTO;
import caixa.psi.service.ProductService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    public Response getAllProducts(@QueryParam("page") @DefaultValue("0") int page,
                                   @QueryParam("size") @DefaultValue("10") int size){

        return productService.getAllProducts(page, size);
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") UUID id) {

        return productService.findById(id);

    }

    @POST
    public Response createProduct(@Valid CreateProductDTO dto) {

        return productService.createProduct(dto);

    }

    @DELETE
    @Path("{id}")
    public Response deleteProduct(@PathParam("id") UUID id){

        return productService.deleteProduct(id);

    }

    @PUT
    @Path("{id}")
    public Response updateProduct(@PathParam("id") UUID id, @Valid UpdateProductDTO dto) {

        return productService.updateProduct(id, dto);

    }

    @PATCH
    @Path("{id}")
    public Response patchProduct(@PathParam("id") UUID id, PatchProductDTO dto) {

        return productService.patchProduct(id, dto);

    }
}
