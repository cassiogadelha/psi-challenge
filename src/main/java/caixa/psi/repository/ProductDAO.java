package caixa.psi.repository;

import caixa.psi.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ProductDAO implements PanacheRepositoryBase<Product, UUID> {
}
