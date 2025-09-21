package caixa.psi.resource;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.PatchProductDTO;
import caixa.psi.dto.ResponseProductDTO;
import caixa.psi.dto.UpdateProductDTO;
import caixa.psi.entity.Product;
import caixa.psi.service.ProductService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

public class ProductResourceTest {

    private ProductService productService = Mockito.mock(ProductService.class);
    private ProductResource productResource = new ProductResource(productService);

    @Test
    void shouldGetProductById() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("4.9"), (short) 36);
        Response fakeResponse = Response.ok(fakeProduct).build();

        Mockito.when(productService.findById(Mockito.any(UUID.class))).thenReturn(fakeResponse);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productResource.findById(UUID.randomUUID());
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeProduct, response.getEntity());
        });
    }

    @Test
    void shouldDeleteProductById() {

        Response fakeResponse = Response.noContent().build();

        Mockito.when(productService.deleteProduct(Mockito.any(UUID.class))).thenReturn(fakeResponse);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productResource.deleteProduct(UUID.randomUUID());

            Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        });
    }

    @Test
    void shouldCreateProductSuccessfully() throws URISyntaxException {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("4.9"), (short) 36);
        Response fakeResponse = Response.created(new URI("fake_URI")).entity(fakeProduct).build();

        Mockito.when(productService.createProduct(Mockito.any(CreateProductDTO.class))).thenReturn(fakeResponse);

        CreateProductDTO fakeDTO = new CreateProductDTO("CDC", new BigDecimal("4.9"), (short) 36);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productResource.createProduct(fakeDTO);
            Assertions.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeProduct, response.getEntity());
        });
    }

    @Test
    void shouldReturnMessageWhenProductsListIsEmpty() {

        Map<String, String> message = Map.of("mensagem", "Não há produtos cadastrados!");
        Response fakeResponse = Response.ok(message).build();

        Mockito.when(productService.getAllProducts(Mockito.any(Integer.class), Mockito.any(Integer.class)))
                .thenReturn(fakeResponse);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productResource.getAllProducts(0, 5);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertTrue(response.getEntity().toString().contains(message.get("mensagem")));
        });
    }

    @Test
    void shouldUpdateProductSuccessfully() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("4.9"), (short) 36);
        UpdateProductDTO fakeUpdate = new UpdateProductDTO("Consignado", new BigDecimal("56.8"), (short) 60);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakeUpdate.name(),
                fakeUpdate.annualInterestRate(),
                fakeUpdate.maxInstallments()
        );

        Response fakeResponse = Response.ok(fakeResponseDTO).build();

        Mockito.when(productService.updateProduct(Mockito.any(UUID.class), Mockito.any(UpdateProductDTO.class)))
                .thenReturn(fakeResponse);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productResource.updateProduct(fakeProduct.getId(), fakeUpdate);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }

    @Test
    void shouldPatchProductSuccessfully() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("4.9"), (short) 36);
        PatchProductDTO fakePatch = new PatchProductDTO("Consignado", new BigDecimal("56.8"), (short) 60);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakePatch.name(),
                fakePatch.annualInterestRate(),
                fakePatch.maxInstallments()
        );

        Response fakeResponse = Response.ok(fakeResponseDTO).build();

        Mockito.when(productService.patchProduct(Mockito.any(UUID.class), Mockito.any(PatchProductDTO.class)))
                .thenReturn(fakeResponse);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productResource.patchProduct(fakeProduct.getId(), fakePatch);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }
}
