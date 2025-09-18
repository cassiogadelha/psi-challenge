package caixa.psi.service;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.UpdateProductDTO;
import caixa.psi.entity.Product;
import caixa.psi.mapper.ProductMapper;
import caixa.psi.repository.ProductDAO;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hibernate.sql.Update;

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

        if(product == null) return Response.status(404).build();

        return Response.ok(productMapper.toResponse(product)).build();
    }

    @Transactional
    public Response createProduct(CreateProductDTO dto){

        Product newProduct = productMapper.toEntity(dto);

        productDAO.persist(newProduct);

        URI location = URI.create("/api/v1/bookings/" + newProduct.getId());

        return Response.created(location)
                .entity(productMapper.toResponse(newProduct))
                .build();
    }

    @Transactional
    public Response deleteProduct(UUID id){

        Product product = productDAO.findById(id);

        if(product == null) return Response.status(404).build();

        productDAO.delete(product);

        return Response.noContent().build();
    }

    @Transactional
    public Response updateProduct(UUID id, UpdateProductDTO dto){
        return Response.ok().build();
    }

}
