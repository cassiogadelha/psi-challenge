package caixa.psi.service;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.ResponseProductDTO;
import caixa.psi.entity.Product;
import caixa.psi.mapper.ProductMapper;
import caixa.psi.repository.ProductDAO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static io.restassured.RestAssured.when;
import static org.mockito.Mockito.mock;

public class ProductServiceTest {

    private ProductMapper productMapper;
    private ProductDAO productDAO;
    private ProductService productService;

    @BeforeEach
    void setup() {
        productMapper = mock(ProductMapper.class);
        productDAO = mock(ProductDAO.class);
        productService = new ProductService(productDAO, productMapper);
    }

    @Test
    void shouldCreateProductSuccesfully() {

        CreateProductDTO dto = new CreateProductDTO(
                "Crédito Pessoal Direto",
                BigDecimal.valueOf(4.5),
                (short)36
        );

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(dto.name());
        product.setAnnualInterestRate(dto.annualInterestRate());
        product.setMaxInstallments(dto.maxInstallments());

        ResponseProductDTO responseDTO = new ResponseProductDTO(
                product.getId(),
                product.getName(),
                product.getAnnualInterestRate(),
                product.getMaxInstallments()
        );

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
    void shouldReturn404NotFoundWhenProductDoesntExist() {

        Mockito.when(productDAO.findById(Mockito.any(UUID.class))).thenReturn(null);

        Response response = productService.findById(UUID.randomUUID());

        Assertions.assertEquals(404, response.getStatus());
        Assertions.assertTrue(response.getEntity().toString().contains("Produto não encontrado!"));

    }
}
