package com.eni.winecellar.repository;

import com.eni.winecellar.bo.wine.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color, Integer> {
}
