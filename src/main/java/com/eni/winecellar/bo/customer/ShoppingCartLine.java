package com.eni.winecellar.bo.customer;

import com.eni.winecellar.bo.wine.Bottle;
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
@Table(name="WINE_CELLAR_SHOPPING_CART_LINE")
public class ShoppingCartLine {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID")
    private Integer id;

    @Column(name="QUANTITY", nullable=true)
    private int quantity;

    @Column(name="PRICE", nullable=true, scale=2)
    private float price;

    @ManyToOne(targetEntity=Bottle.class, fetch=FetchType.LAZY)
    @JoinColumn(name="BOTTLE_ID", referencedColumnName="ID")
    private Bottle bottle;
}
