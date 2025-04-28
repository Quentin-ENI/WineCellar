package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.Customer;
import com.eni.winecellar.bo.customer.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
//    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.customer.username = :customerUsername AND sc.orderNumber IS NULL")
//    List<ShoppingCart> findByCustomerUsername(@Param("customerUsername") String customerUsername, @Param("orderNumber") String OrderNumber);

    /**
     * Get all shopping carts for a specific customer which did not lead to an order
     * @param customerUsername
     * @return
     */
    List<ShoppingCart> findByCustomerUsernameAndOrderNumberIsNull(String customerUsername);

//    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.customer.username = :customerUsername AND sc.orderNumber IS NOT NULL")
//    List<ShoppingCart> findByCustomerUsername(@Param("customerUsername") String customerUsername, @Param("orderNumber") String OrderNumber);

    /**
     * Get all shopping carts for a specific customer which lead to an order
     * @param customerUsername
     * @return
     */
    List<ShoppingCart> findByCustomerUsernameAndOrderNumberIsNotNull(String customerUsername);

    List<ShoppingCart> findByOrderNumberNullAndCustomer(Customer customer);
}
