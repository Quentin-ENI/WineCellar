package com.eni.winecellar.bo.wine;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Entity
@Table(name="WINE_CELLAR_COLOR")
public class Color {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID")
    private Integer id;

    @Column(name="NAME", nullable=false, unique=true, length=250)
    private String name;
}
