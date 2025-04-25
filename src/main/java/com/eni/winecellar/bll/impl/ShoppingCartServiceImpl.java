package com.eni.winecellar.bll.impl;

import com.eni.winecellar.bll.ShoppingCartService;
import com.eni.winecellar.bo.customer.Customer;
import com.eni.winecellar.bo.customer.ShoppingCart;
import com.eni.winecellar.bo.customer.ShoppingCartLine;
import com.eni.winecellar.repository.CustomerRepository;
import com.eni.winecellar.repository.ShoppingCartLineRepository;
import com.eni.winecellar.repository.ShoppingCartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The owner can access all requests
 */
@Service
@AllArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private ShoppingCartRepository shoppingCartRepository;
    private ShoppingCartLineRepository shoppingCartLineRepository;
    private CustomerRepository customerRepository;

    /**
     * Allows customer and owner to load a specific shopping cart
     * @param idShoppingCart
     * @return
     */
    @Override
    public ShoppingCart loadShoppingCart(int idShoppingCart) {
        if (idShoppingCart <= 0) {
            throw new RuntimeException("Identifiant n'existe pas");
        }

        final Optional<ShoppingCart> opt = shoppingCartRepository.findById(idShoppingCart);
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new RuntimeException("Aucun ShoppingCart ne correspond");
    }

    /**
     * Allows customer and owner to load all commands from a specific customer
     * @param idCustomer
     * @return
     */
    @Override
    public List<ShoppingCart> loadOrders(String idCustomer) {
        if (idCustomer == null) {
            throw new RuntimeException("Client n'existe pas");
        }
        return shoppingCartRepository.findByCustomerUsernameAndOrderNumberIsNotNull(idCustomer);
    }

    @Override
    public List<ShoppingCart> loadShoppingCartsNotPaid(String idCustomer) {
        final Customer customer = validCustomer(idCustomer);
        return shoppingCartRepository.findByOrderNumberNullAndCustomer(customer);
    }

    /**
     * Allows a customer or owner to save or update a shopping cart
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart saveShoppingCart(ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            throw new RuntimeException("ShoppingCart n'existe pas");
        }

        if (shoppingCart.getCustomer() == null || shoppingCart.getCustomer().getUsername() == null) {
            throw new RuntimeException("Client n'existe pas");
        }
        final Customer customerDB = validCustomer(shoppingCart.getCustomer().getUsername());
        shoppingCart.setCustomer(customerDB);

        final List<ShoppingCartLine> shoppingCartLines = shoppingCart.getShoppingCartLines();
        validShoppingCartLines(shoppingCartLines);

        final ShoppingCart shoppingCartDB = shoppingCartRepository.save(shoppingCart);
        return shoppingCartDB;
    }

    /**
     * Allows a customer or owner to validate a shopping cart
     * Creates an order
     * @param ShoppingCart
     * @return
     */
    @Override
    public ShoppingCart placeAnOrder(ShoppingCart ShoppingCart) {
        // Validation du ShoppingCart
        if (ShoppingCart == null) {
            throw new RuntimeException("ShoppingCart n'existe pas");
        }

        // Validation du client
        if (ShoppingCart.getCustomer() == null || ShoppingCart.getCustomer().getUsername() == null) {
            throw new RuntimeException("Client n'existe pas");
        }
        final Customer customerDB = validCustomer(ShoppingCart.getCustomer().getUsername());
        ShoppingCart.setCustomer(customerDB);

        final List<ShoppingCartLine> shoppingCartLines = ShoppingCart.getShoppingCartLines();
        validShoppingCartLines(shoppingCartLines);

        ShoppingCart.setPaid(true);
        ShoppingCart.setOrderNumber(ShoppingCart.getCustomer().getUsername() + "_" + ShoppingCart.getId());

        final ShoppingCart shoppingCartDB = shoppingCartRepository.save(ShoppingCart);

        return shoppingCartDB;
    }

    /**
     * Check if a customer is valid from its id then returns it
     * @param idCustomer
     * @return
     */
    private Customer validCustomer(String idCustomer) {
        if (idCustomer == null) {
            throw new RuntimeException("Identifiant n'existe pas");
        }

        final Optional<Customer> opt = customerRepository.findById(idCustomer);
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new RuntimeException("Aucun client ne correspond");
    }

    /**
     * Check if all shopping cart lines are valid
     * @param shoppingCartLines
     */
    private void validShoppingCartLines(List<ShoppingCartLine> shoppingCartLines) {
        if (shoppingCartLines == null || shoppingCartLines.isEmpty()) {
            throw new RuntimeException("Il faut au moins une ligne dans le ShoppingCart");
        }

        shoppingCartLines.forEach(shoppingCartLine ->{
            if(shoppingCartLine.getId() != null) {
                final Optional<ShoppingCartLine> opt = shoppingCartLineRepository.findById(shoppingCartLine.getId());
                if (opt.isPresent()) {
                    shoppingCartLine = opt.get();
                }
            }
        });
    }
}
