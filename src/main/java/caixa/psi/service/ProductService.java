package caixa.psi.service;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.PatchProductDTO;
import caixa.psi.dto.UpdateProductDTO;
import caixa.psi.entity.Product;
import caixa.psi.mapper.ProductMapper;
import caixa.psi.repository.ProductDAO;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.hibernate.sql.Update;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class ProductService {

    private final ProductDAO productDAO;
    private final ProductMapper productMapper;

    public ProductService(ProductDAO productDAO, ProductMapper productMapper) {
        this.productDAO = productDAO;
        this.productMapper = productMapper;
    }

    public Response getAllProducts(int page, int size) {

        List<Product> products = productDAO.findAll().page(Page.of(page, size)).list();

        if (products.isEmpty()) {
            Map<String, String> response = Map.of("mensagem", "Não há produtos cadastrados!");
            return Response.ok(response).build();
        }

        return Response.ok(productMapper.toResponseList(products)).build();
    }

    public Response findById(UUID id) {
        Product product = productDAO.findById(id);

        if(product == null) {
            Map<String, String> response = Map.of("mensagem", "Produto não encontrado!");
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        }

        return Response.ok(productMapper.toResponse(product)).build();
    }

    @Transactional
    public Response createProduct(CreateProductDTO dto){

        Product newProduct = productMapper.toEntity(dto);

        productDAO.persist(newProduct);

        URI location = URI.create("/api/v1/product/" + newProduct.getId());

        return Response.created(location)
                .entity(productMapper.toResponse(newProduct))
                .build();
    }

    @Transactional
    public Response deleteProduct(UUID id){

        Product product = productDAO.findById(id);

        if(product == null) {
            Map<String, String> message = Map.of("mensagem", "Produto não encontrado!");
            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }

        productDAO.delete(product);

        return Response.noContent().build();
    }

    @Transactional
    public Response updateProduct(UUID id, UpdateProductDTO dto){

        Product product = productDAO.findById(id);

        if (product == null) {
            Map<String, String> message = Map.of("mensagem", "Produto não encontrado!");
            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }

        product.setName(dto.name());
        product.setAnnualInterestRate(dto.annualInterestRate());
        product.setMaxInstallments(dto.maxInstallments());

        return Response.ok(productMapper.toResponse(product)).build();
    }

    @Transactional
    public Response patchProduct(UUID id, PatchProductDTO dto) {

        Product product = productDAO.findById(id);

        if (product == null) {
            Map<String, String> message = Map.of("mensagem", "Produto não encontrado!");
            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            product.setName(dto.name());
        }

        if (dto.annualInterestRate() != null && dto.annualInterestRate().longValue() > 0.0) {
            product.setAnnualInterestRate(dto.annualInterestRate());
        }

        if (dto.maxInstallments() > 0) {
            product.setMaxInstallments(dto.maxInstallments());
        }

        return Response.ok(productMapper.toResponse(product)).build();

    }

    public Product getProduct(UUID productId) {
        Product product = productDAO.findById(productId);

        if(product == null) throw new NotFoundException();

        return product;
    }

}
