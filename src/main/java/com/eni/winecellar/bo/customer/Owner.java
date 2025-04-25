package com.eni.winecellar.bo.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@Table(name="WINE_CELLAR_OWNER")
public class Owner extends User {
    @Column(name="SIRET", nullable=false, length=14)
    private String siret;
}
