package com.eni.winecellar.repository;

import com.eni.winecellar.bo.wine.Bottle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BottleRepository extends JpaRepository<Bottle, Integer> {
    @Query("SELECT b FROM Bottle b WHERE b.region.id = :regionId")
    List<Bottle> findByRegion(@Param("regionId") int regionId);

    @Query("SELECT b FROM Bottle b WHERE b.color.id = :colorId")
    List<Bottle> findByColor(@Param("colorId") int colorId);
}
