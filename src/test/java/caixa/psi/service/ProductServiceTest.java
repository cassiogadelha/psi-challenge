package caixa.psi.service;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.ResponseProductDTO;
import caixa.psi.entity.Product;
import caixa.psi.mapper.ProductMapper;
import caixa.psi.repository.ProductDAO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.apache.groovy.json.internal.Exceptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.when;
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
}
