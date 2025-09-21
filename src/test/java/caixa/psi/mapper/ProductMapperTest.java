package caixa.psi.mapper;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.ResponseProductDTO;
import caixa.psi.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductMapperTest {

    ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void shouldConvertCreateProductDTOToEntity() {

        CreateProductDTO dto = new CreateProductDTO("CDC", new BigDecimal(67), (short)45);

        Assertions.assertDoesNotThrow(() -> {
            Product product = productMapper.toEntity(dto);

            Assertions.assertEquals(dto.name(), product.getName());
            Assertions.assertEquals(dto.annualInterestRate(), product.getAnnualInterestRate());
            Assertions.assertEquals(dto.maxInstallments(), product.getMaxInstallments());
        });

    }

    @Test
    void shouldConvertEntityToResponseProductDTO() {

        Product product = new Product(UUID.randomUUID(),"CDC", new BigDecimal(67), (short)45);

        Assertions.assertDoesNotThrow(() -> {
            ResponseProductDTO dto = productMapper.toResponse(product);

            Assertions.assertEquals(product.getName(), dto.name());
            Assertions.assertEquals(product.getAnnualInterestRate(), dto.annualInterestRate());
            Assertions.assertEquals(product.getMaxInstallments(), dto.maxInstallments());
        });

    }

    @Test
    void shouldConvertProductsListToResponseProductDTOList() {

        Product product = new Product(UUID.randomUUID(),"CDC", new BigDecimal(67), (short)45);
        Product product2 = new Product(UUID.randomUUID(),"Financiamento Habitacional", new BigDecimal(25), (short)17);

        List<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);

        Assertions.assertDoesNotThrow(() -> {
            List<ResponseProductDTO> responseList = productMapper.toResponseList(products);
            Assertions.assertFalse(responseList.isEmpty());

            for (int i = 0; i < products.size(); i ++) {
                Assertions.assertEquals(products.get(i).getId(), responseList.get(i).id());
                Assertions.assertEquals(products.get(i).getName(), responseList.get(i).name());
                Assertions.assertEquals(products.get(i).getName(), responseList.get(i).name());
            }
        });

    }
}
