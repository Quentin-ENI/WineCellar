package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
