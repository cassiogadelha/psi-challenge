package caixa.psi.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductTest {

    @Test
    void shouldCreateProductSuccessfully() {

        Product product = new Product(UUID.randomUUID(), "CDC", new BigDecimal("7.8"), (short) 48);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals("CDC", product.getName());
        Assertions.assertEquals(new BigDecimal("7.8"), product.getAnnualInterestRate());
        Assertions.assertEquals((short) 48, product.getMaxInstallments());
    }

    @Test
    void shouldCreateProductWithoutId() {
        Product product = new Product("CDC", new BigDecimal("7.8"), (short) 48);

        Assertions.assertNull(product.getId());
        Assertions.assertEquals("CDC", product.getName());
        Assertions.assertEquals(new BigDecimal("7.8"), product.getAnnualInterestRate());
        Assertions.assertEquals((short) 48, product.getMaxInstallments());
    }
}
