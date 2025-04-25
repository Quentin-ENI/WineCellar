package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
