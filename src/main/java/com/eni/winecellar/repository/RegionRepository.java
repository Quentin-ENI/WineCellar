package com.eni.winecellar.repository;

import com.eni.winecellar.bo.wine.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Integer> {
}
