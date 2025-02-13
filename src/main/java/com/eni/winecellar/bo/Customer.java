package com.eni.winecellar.bo;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Entity
@Table(name="customers")
public class Customer {
    @Id
    @Column(name="username", nullable=false, length=50, unique=true)
    private String username;

    @Column(name="password", nullable=false, length=255)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String password;

    @Column(name="lastname", nullable=true)
    @EqualsAndHashCode.Exclude
    private String lastname;

    @Column(name="firstname", nullable=true)
    @EqualsAndHashCode.Exclude
    private String firstname;
}
