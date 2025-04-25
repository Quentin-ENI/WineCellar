package com.eni.winecellar.bo.customer;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Entity
@Table(name="WINE_CELLAR_ADDRESS")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Integer id;

    @Column(name="STREET", length=250, nullable=false)
    private String street;

    @Column(name="ZIP_CODE", length=5, nullable=false)
    private String zipCode;

    @Column(name="CITY", length=150, nullable=false)
    private String city;
}
