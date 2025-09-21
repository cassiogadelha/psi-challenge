package caixa.psi.service;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.PatchProductDTO;
import caixa.psi.dto.ResponseProductDTO;
import caixa.psi.dto.UpdateProductDTO;
import caixa.psi.entity.Product;
import caixa.psi.mapper.ProductMapper;
import caixa.psi.repository.ProductDAO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class ProductServiceTest {

    private ProductMapper productMapper;
    private ProductDAO productDAO;
    private ProductService productService;

    @BeforeEach
    void setup() {
        this.productMapper = mock(ProductMapper.class);
        this.productDAO = mock(ProductDAO.class);
        this.productService = new ProductService(productDAO, productMapper);
    }

    private CreateProductDTO getFakeProductDTO() {

        return new CreateProductDTO(
                "Crédito Pessoal Direto",
                BigDecimal.valueOf(4.5),
                (short)36
        );

    }

    private Product getFakeProduct(CreateProductDTO dto) {

        return new Product(
                UUID.randomUUID(),
                dto.name(),
                dto.annualInterestRate(),
                dto.maxInstallments()
        );

    }

    private ResponseProductDTO getFakeResponseProductDTO(Product product) {

        return new ResponseProductDTO(
                product.getId(),
                product.getName(),
                product.getAnnualInterestRate(),
                product.getMaxInstallments()
        );

    }

    @Test
    void shouldCreateProductSuccessfully() {

        CreateProductDTO dto = getFakeProductDTO();

        Product product = getFakeProduct(dto);

        ResponseProductDTO responseDTO = getFakeResponseProductDTO(product);

        Mockito.when(productMapper.toEntity(dto)).thenReturn(product);
        Mockito.when(productMapper.toResponse(product)).thenReturn(responseDTO);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.createProduct(dto);
            Assertions.assertEquals(201, response.getStatus());
            Assertions.assertEquals(responseDTO, response.getEntity());
        });

        Mockito.verify(productDAO).persist(product);

    }

    @Test
    void shouldReturnMessageWhenProductListIsEmpty() {

        PanacheQuery<Product> panacheQueryMock = mock(PanacheQuery.class);

        Mockito.when(productDAO.findAll()).thenReturn(panacheQueryMock);
        Mockito.when(panacheQueryMock.page(Mockito.any(Page.class))).thenReturn(panacheQueryMock);
        Mockito.when(panacheQueryMock.list()).thenReturn(Collections.emptyList());


        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.getAllProducts(0, 5);
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertTrue(response.getEntity().toString().contains("Não há produtos cadastrados!"));
        });

    }

    @Test
    void shouldReturn404NotFoundWhenProductDoesNotExist() {

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(null);

        Response response = productService.findById(UUID.randomUUID());

        Assertions.assertEquals(404, response.getStatus());
        Assertions.assertTrue(response.getEntity().toString().contains("Produto não encontrado!"));

    }

    @Test
    void shouldReturnProductWhenItExists() {

        Product fakeProduct = getFakeProduct(getFakeProductDTO());

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(fakeProduct)).thenReturn(getFakeResponseProductDTO(fakeProduct));

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.findById(fakeProduct.getId());
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals(getFakeResponseProductDTO(fakeProduct), response.getEntity());
        });

    }

    @Test
    void shouldReturnListOfProductsWhenItExists() {

        Product product = getFakeProduct(getFakeProductDTO());
        Product product2 = getFakeProduct(getFakeProductDTO());

        List<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);

        List<ResponseProductDTO> dtoList = new ArrayList<>();
        dtoList.add(getFakeResponseProductDTO(product));
        dtoList.add(getFakeResponseProductDTO(product2));

        PanacheQuery<Product> panacheQueryMock = mock(PanacheQuery.class);

        Mockito.when(productDAO.findAll()).thenReturn(panacheQueryMock);
        Mockito.when(panacheQueryMock.page(Mockito.any(Page.class))).thenReturn(panacheQueryMock);
        Mockito.when(panacheQueryMock.list()).thenReturn(products);
        Mockito.when(productMapper.toResponseList(products)).thenReturn(dtoList);


        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.getAllProducts(0, 5);
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals(dtoList, response.getEntity());
        });
    }

    @Test
    void shouldDeleteExistentProductSuccessfully() {

        Product product = getFakeProduct(getFakeProductDTO());

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(product);
        Mockito.doNothing().when(productDAO).delete(Mockito.any(Product.class));

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.deleteProduct(UUID.randomUUID());
            Assertions.assertEquals(204, response.getStatus());
        });
    }

    @Test
    void shouldReturnExceptionWhenDeletingNotFoundProduct() {

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(null);

        Response response = productService.deleteProduct(UUID.randomUUID());

        Assertions.assertEquals(404, response.getStatus());
        Assertions.assertTrue(response.getEntity().toString().contains("Produto não encontrado!"));
    }

    @Test
    void shouldGetProductSuccessfully() {

        Product product = getFakeProduct(getFakeProductDTO());

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(product);

        Assertions.assertEquals(productService.getProduct(UUID.randomUUID()), product);
    }

    @Test
    void shouldThrowsExceptionWhenProductDoesNotExistsOnGetProduct() {

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> {
            productService.getProduct(UUID.randomUUID());
        });

    }

    @Test
    void shouldUpdateProductSuccessfully() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("57.0"), (short) 48);
        UpdateProductDTO fakeUpdate = new UpdateProductDTO("CDC", new BigDecimal("40.0"), (short) 36);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakeUpdate.name(),
                fakeUpdate.annualInterestRate(),
                fakeUpdate.maxInstallments()
        );

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(Mockito.any(Product.class))).thenReturn(fakeResponseDTO);
        Mockito.when(productMapper.toEntity(Mockito.any(UpdateProductDTO.class))).thenReturn(fakeProduct);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.updateProduct(UUID.randomUUID(), fakeUpdate);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }

    @Test
    void shouldFailUpdateWhenProductDoesNotExist() {

        UpdateProductDTO fakeUpdate = new UpdateProductDTO("CDC", new BigDecimal("40.0"), (short) 36);

        Response response = productService.updateProduct(UUID.randomUUID(), fakeUpdate);

        Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        Assertions.assertTrue(response.getEntity().toString().contains("Produto não encontrado!"));

    }

    @Test
    void shouldPatchProductSuccessfully() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("57.0"), (short) 48);
        PatchProductDTO fakePatch = new PatchProductDTO("CDC", new BigDecimal("40.0"), (short) 36);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakePatch.name(),
                fakePatch.annualInterestRate(),
                fakePatch.maxInstallments()
        );

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(Mockito.any(Product.class))).thenReturn(fakeResponseDTO);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.patchProduct(UUID.randomUUID(), fakePatch);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });
    }

    @Test
    void shouldPatchProductCorrectlyWhenNameIsNotProvided() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("57.0"), (short) 48);
        PatchProductDTO fakePatch = new PatchProductDTO("", new BigDecimal("40.0"), (short) 36);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakeProduct.getName(),
                fakePatch.annualInterestRate(),
                fakePatch.maxInstallments()
        );

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(Mockito.any(Product.class))).thenReturn(fakeResponseDTO);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.patchProduct(UUID.randomUUID(), fakePatch);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }

    @Test
    void shouldPatchProductCorrectlyWhenAnnualInterestRateIsNotProvided() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("57.0"), (short) 48);
        PatchProductDTO fakePatch = new PatchProductDTO("Financiamento", null, (short) 36);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakePatch.name(),
                fakeProduct.getAnnualInterestRate(),
                fakePatch.maxInstallments()
        );

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(Mockito.any(Product.class))).thenReturn(fakeResponseDTO);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.patchProduct(UUID.randomUUID(), fakePatch);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }

    @Test
    void shouldPatchProductCorrectlyWhenAnnualInterestRateIsZero() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("57.0"), (short) 48);
        PatchProductDTO fakePatch = new PatchProductDTO("Financiamento", new BigDecimal("0.0"), (short) 36);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakePatch.name(),
                fakeProduct.getAnnualInterestRate(),
                fakePatch.maxInstallments()
        );

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(Mockito.any(Product.class))).thenReturn(fakeResponseDTO);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.patchProduct(UUID.randomUUID(), fakePatch);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }

    @Test
    void shouldPatchProductCorrectlyWhenMaxInstallmentsIsNotProvided() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("57.0"), (short) 48);
        PatchProductDTO fakePatch = new PatchProductDTO("Financiamento", new BigDecimal("40.0"), (short) 0);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakePatch.name(),
                fakePatch.annualInterestRate(),
                fakeProduct.getMaxInstallments()
        );

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(Mockito.any(Product.class))).thenReturn(fakeResponseDTO);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.patchProduct(UUID.randomUUID(), fakePatch);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }

    @Test
    void shouldPatchProductCorrectlyWhenMaxInstallmentsIsNegative() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("57.0"), (short) 48);
        PatchProductDTO fakePatch = new PatchProductDTO("Financiamento", new BigDecimal("40.0"), (short) -8);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakePatch.name(),
                fakePatch.annualInterestRate(),
                fakeProduct.getMaxInstallments()
        );

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(Mockito.any(Product.class))).thenReturn(fakeResponseDTO);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.patchProduct(UUID.randomUUID(), fakePatch);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }

    @Test
    void shouldPatchProductCorrectlyWhenAnnualInterestRateIsNegative() {

        Product fakeProduct = new Product(UUID.randomUUID(), "CDC", new BigDecimal("57.0"), (short) 48);
        PatchProductDTO fakePatch = new PatchProductDTO("Financiamento", new BigDecimal("-7.0"), (short) 45);

        ResponseProductDTO fakeResponseDTO = new ResponseProductDTO(
                fakeProduct.getId(),
                fakePatch.name(),
                fakeProduct.getAnnualInterestRate(),
                fakePatch.maxInstallments()
        );

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(fakeProduct);
        Mockito.when(productMapper.toResponse(Mockito.any(Product.class))).thenReturn(fakeResponseDTO);

        Assertions.assertDoesNotThrow(() -> {
            Response response = productService.patchProduct(UUID.randomUUID(), fakePatch);
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(fakeResponseDTO, response.getEntity());
        });

    }

}
