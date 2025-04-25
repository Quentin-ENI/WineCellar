package com.eni.winecellar.bll;

import com.eni.winecellar.bo.customer.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    ShoppingCart loadShoppingCart(int idShoppingCart);
    List<ShoppingCart> loadOrders(String idCustomer);
    List<ShoppingCart> loadShoppingCartsNotPaid(String idCustomer);
    ShoppingCart saveShoppingCart(ShoppingCart shoppingCart);
    ShoppingCart placeAnOrder(ShoppingCart ShoppingCart);
}
