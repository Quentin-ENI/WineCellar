package com.eni.winecellar.controller;

import com.eni.winecellar.bll.ShoppingCartService;
import com.eni.winecellar.bo.customer.ShoppingCart;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/winecellar/shopping_carts")
public class ShoppingCartController {
    private ShoppingCartService shoppingCartService;

    @GetMapping("/{id}")
    public ResponseEntity<?> searchShoppingCartById(
            @PathVariable("id") String shoppingCartId
    ) {
        // Toutes les données transmises par le protocole HTTP sont des chaines de
        // caractères par défaut
        // Il vaut mieux gérer les exceptions des données dans la méthode
        try {
            int id = Integer.parseInt(shoppingCartId);
            final ShoppingCart emp = shoppingCartService.loadShoppingCart(id);
            return ResponseEntity.ok(emp);
        } catch (NumberFormatException e) {
            // Statut 406 : No Acceptable
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Votre identifiant n'est pas un entier");
        }
    }

    @GetMapping("/customer/active/{id}")
    public ResponseEntity<?> searchShoppingCartsNotPaidByCustomer(
            @PathVariable("id") String idCustomer
    ) {
        final List<ShoppingCart> ShoppingCarts = shoppingCartService.loadShoppingCartsNotPaid(idCustomer);
        if (ShoppingCarts == null || ShoppingCarts.isEmpty()) {
            // Statut 204 : No Content - Pas de body car rien à afficher
            return ResponseEntity.noContent().build();
        }
        // Statut 200 : OK + dans le body ShoppingCarts
        // Le contenu du body est directement injecté dans la méthode ok
        return ResponseEntity.ok(ShoppingCarts);
    }

    @GetMapping("/customer/orders/{id}")
    public ResponseEntity<?> searchOrdersCustomer(
            @PathVariable("id") String idCustomer
    ) {
        final List<ShoppingCart> ShoppingCarts = shoppingCartService.loadOrders(idCustomer);
        if (ShoppingCarts == null || ShoppingCarts.isEmpty()) {
            // Statut 204 : No Content - Pas de body car rien à afficher
            return ResponseEntity.noContent().build();
        }
        // Statut 200 : OK + dans le body ShoppingCarts
        // Le contenu du body est directement injecté dans la méthode ok
        return ResponseEntity.ok(ShoppingCarts);
    }

    @PostMapping
    public ResponseEntity<?> addShoppingCart(
            @Valid @RequestBody ShoppingCart ShoppingCart
    ) {
        shoppingCartService.saveShoppingCart(ShoppingCart);
        return ResponseEntity.ok(ShoppingCart);
    }

    @PutMapping
    public ResponseEntity<?> placeAnOrder(
            @Valid @RequestBody ShoppingCart ShoppingCart
    ) {
        shoppingCartService.placeAnOrder(ShoppingCart);
        return ResponseEntity.ok(ShoppingCart);
    }
}
