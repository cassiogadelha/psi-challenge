package caixa.psi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalculationMemory {

    int month;
    float initialLoanBalance;
    float interestRate;
    float amortization;
    float finalLoanBalance;

}
