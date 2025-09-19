package caixa.psi.mapper;

import caixa.psi.dto.CreateProductDTO;
import caixa.psi.dto.ResponseLoanDataDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface LoanMapper {

    ResponseLoanDataDTO toEntity(CreateProductDTO dto);

}
