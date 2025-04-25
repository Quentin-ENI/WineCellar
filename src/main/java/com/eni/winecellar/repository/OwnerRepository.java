package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {
}
