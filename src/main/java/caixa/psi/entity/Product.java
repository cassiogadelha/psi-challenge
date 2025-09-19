package caixa.psi.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_product")
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;
    BigDecimal annualInterestRate;
    short maxInstallments;

    public Product(String name, BigDecimal annualInterestRate, short maxInstallments) {
        this.name = name;
        this.annualInterestRate = annualInterestRate;
        this.maxInstallments = maxInstallments;
    }
}
