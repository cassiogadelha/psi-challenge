package caixa.psi.resource;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.service.ProductService;
import jakarta.decorator.Delegate;
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
    public Response createProduct(CreateProductDTO dto) {

        return productService.createProduct(dto);

    }

    @DELETE
    @Path("{id}")
    public Response deleteProduct(UUID id){

        return productService.deleteProduct(id);

    }
}
