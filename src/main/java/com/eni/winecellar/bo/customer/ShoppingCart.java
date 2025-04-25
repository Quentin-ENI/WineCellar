package com.eni.winecellar.bo.customer;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Entity
@Table(name="WINE_CELLAR_SHOPPING_CART")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Integer id;

    @Column(name="ORDER_NUMBER", nullable=true, length=200, unique=true)
    private String orderNumber;

    @Column(name="TOTAL_PRICE", nullable=true, scale=2)
    private float totalPrice;

    @Column(name="IS_PAID", nullable=true)
    private boolean isPaid;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
    @JoinColumn(name="SHOPPING_CART_LINE_ID", referencedColumnName="ID")
    @Builder.Default
    private List<ShoppingCartLine> shoppingCartLines = new ArrayList();

    @ManyToOne(targetEntity=Customer.class, fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name="CUSTOMER_USERNAME", referencedColumnName="USERNAME")
    private Customer customer;
}
