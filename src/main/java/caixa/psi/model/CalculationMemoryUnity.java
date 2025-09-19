package caixa.psi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalculationMemoryUnity {

    int month;
    BigDecimal initialLoanBalance;
    BigDecimal interest;
    BigDecimal amortization;
    BigDecimal finalLoanBalance;

}
