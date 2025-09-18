package caixa.psi.mapper;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.ResponseProductDTO;
import caixa.psi.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ProductMapper {

    Product toEntity(CreateProductDTO dto);

    ResponseProductDTO toResponse(Product product);

    List<ResponseProductDTO> toResponseList(List<Product> products);
}
