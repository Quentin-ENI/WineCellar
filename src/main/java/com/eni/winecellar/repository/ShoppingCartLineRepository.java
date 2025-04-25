package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.ShoppingCartLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartLineRepository extends JpaRepository<ShoppingCartLine, Integer> {
}
