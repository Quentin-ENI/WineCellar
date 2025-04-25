package com.eni.winecellar.bo.customer;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@Entity
@Table(name="WINE_CELLAR_USER")
@Inheritance(strategy=InheritanceType.JOINED)
public class User {
    @Id
    @Column(name="USERNAME", nullable=false, length=255, unique=true)
    private String username;

    @Column(name="PASSWORD", nullable=false, length=68)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String password;

    @Column(name="LASTNAME", nullable=false, length=90)
    @EqualsAndHashCode.Exclude
    private String lastname;

    @Column(name="FIRSTNAME", nullable=false, length=150)
    @EqualsAndHashCode.Exclude
    private String firstname;

    @Column(name="AUTHORITY", nullable=true, length=15)
    @EqualsAndHashCode.Exclude
    private String authority;
}
