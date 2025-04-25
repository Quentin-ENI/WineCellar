package com.eni.winecellar.bo.wine;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Entity
@Table(name="WINE_CELLAR_BOTTLE")
public class Bottle {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID")
    private Integer id;

    @NotBlank(message="{bottle.name.blank-error}")
    @Size(min=0, max=255, message="{bottle.name.size-error}")
    @Column(name="NAME", nullable=false, unique=true, length=250)
    private String name;

    @Column(name="IS_SPARKLING", nullable=false)
    private boolean isSparkling;

    @Size(min=0, max=100, message="{bottle.vintage.size-error}")
    @Column(name="VINTAGE", nullable=false, length=100)
    private String vintage;

    @Min(value=1, message="{bottle.quantity.min-error}")
    @Column(name="QUANTITY", nullable=false)
    private int quantity;

    @Min(value=1, message="{bottle.price.min-error}")
    @Column(name="PRICE", nullable=false, scale=2)
    private float price;

    @NotNull(message="{bottle.region.not-null}")
    @ManyToOne(targetEntity=Region.class, fetch=FetchType.EAGER)
    @JoinColumn(name="REGION_ID", referencedColumnName="ID")
    @EqualsAndHashCode.Exclude
    private Region region;

    @NotNull(message="{bottle.color.not-null}")
    @ManyToOne(targetEntity=Color.class, fetch=FetchType.EAGER)
    @JoinColumn(name="COLOR_ID", referencedColumnName="ID")
    @EqualsAndHashCode.Exclude
    private Color color;

}
