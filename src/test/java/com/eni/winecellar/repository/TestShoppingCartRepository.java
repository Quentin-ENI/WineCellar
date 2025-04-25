package com.eni.winecellar.repository;

import com.eni.winecellar.bo.customer.Address;
import com.eni.winecellar.bo.customer.Customer;
import com.eni.winecellar.bo.customer.ShoppingCart;
import com.eni.winecellar.bo.customer.ShoppingCartLine;
import com.eni.winecellar.bo.wine.Bottle;
import com.eni.winecellar.bo.wine.Color;
import com.eni.winecellar.bo.wine.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TestShoppingCartRepository {

    private static final Logger logger = LoggerFactory.getLogger(TestShoppingCartRepository.class);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    ShoppingCartRepository shoppingCartRepository;

    @Autowired
    BottleRepository bottleRepository;

    @Autowired
    CustomerRepository customerRepository;

    Region paysDeLaLoire;
    Color white;
    List<Bottle> bottles;

    Customer tom;

    @BeforeEach
    public void initializationDB() {
        loadDataBottles();
        loadDataShoppingCartsCustomers();
    }

    @Test
    public void test_saveShoppingCart_newShoppingCartLine_newShoppingCart() {
        ShoppingCartLine shoppingCartLine = ShoppingCartLine.builder()
                .quantity(2)
                .price(4.42f)
                .build();

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .orderNumber("AZERTY1234")
                .totalPrice(shoppingCartLine.getPrice() * shoppingCartLine.getQuantity())
                .isPaid(true)
                .shoppingCartLines(List.of(shoppingCartLine))
                .build();

        shoppingCartRepository.save(shoppingCart);
        final ShoppingCart savedShoppingCart = this.entityManager.find(ShoppingCart.class, shoppingCart.getId());

        assertThat(shoppingCart.getId()).isGreaterThan(0);
        assertThat(shoppingCart).isEqualTo(savedShoppingCart);
    }

    @Test
    public void test_saveShoppingCart_newShoppingCartLine_existingShoppingCart() {
        ShoppingCart shoppingCart = this.shoppingCartDB();

        ShoppingCartLine shoppingCartLine = ShoppingCartLine.builder()
                .quantity(2)
                .price(4.42f)
                .build();

        shoppingCart.getShoppingCartLines().add(shoppingCartLine);

        shoppingCartRepository.save(shoppingCart);
        final ShoppingCart savedShoppingCart = this.entityManager.find(ShoppingCart.class, shoppingCart.getId());

        assertThat(shoppingCart).isEqualTo(savedShoppingCart);
    }

    @Test
    public void test_delete() {
        ShoppingCart shoppingCart = this.shoppingCartDB();

        shoppingCartRepository.delete(shoppingCart);
        final ShoppingCart savedShoppingCart = this.entityManager.find(ShoppingCart.class, shoppingCart.getId());

        assertThat(savedShoppingCart).isNull();
    }

    @Test
    public void test_orphanRemoval() {
        ShoppingCart shoppingCart = this.shoppingCartDB();

        shoppingCartRepository.delete(shoppingCart);
        final ShoppingCartLine shoppingCartLine = this.entityManager.find(ShoppingCartLine.class, shoppingCart.getShoppingCartLines().getFirst().getId());

        assertThat(shoppingCartLine).isNull();
    }

    @Test
    public void test_saveOneShoppingCart() {
        ShoppingCart shoppingCart = shoppingCartData().getFirst();
        shoppingCartRepository.save(shoppingCart);
        final ShoppingCart shoppingCartSaved = entityManager.find(ShoppingCart.class, shoppingCart.getId());
        logger.info(shoppingCartSaved.toString());
        assertEquals(shoppingCart, shoppingCartSaved);
        assertEquals(shoppingCart.getCustomer(), shoppingCartSaved.getCustomer());
    }

    @Test
    public void test_multipleShoppingCartsAndOneCustomer() {
        List<ShoppingCart> shoppingCarts = shoppingCartData();
        shoppingCartRepository.saveAll(shoppingCarts);
        final ShoppingCart firstShoppingCartSaved = entityManager.find(ShoppingCart.class, shoppingCarts.getFirst().getId());
        assertEquals(shoppingCarts.getFirst().getId(), firstShoppingCartSaved.getId());
        assertEquals(shoppingCarts.getFirst().getCustomer().getUsername(), firstShoppingCartSaved.getCustomer().getUsername());
        final ShoppingCart secondShoppingCartSaved = entityManager.find(ShoppingCart.class, shoppingCarts.getLast().getId());
        assertEquals(shoppingCarts.getLast().getId(), secondShoppingCartSaved.getId());
        assertEquals(shoppingCarts.getLast().getCustomer().getUsername(), secondShoppingCartSaved.getCustomer().getUsername());
        assertEquals(shoppingCarts.getFirst().getCustomer().getUsername(), shoppingCarts.getLast().getCustomer().getUsername());
    }

    @Test
    public void test_delete_customer() {
        List<ShoppingCart> shoppingCarts = shoppingCartData();
        shoppingCartRepository.saveAll(shoppingCarts);
        Customer customer = shoppingCarts.getFirst().getCustomer();

        customerRepository.delete(customer);

        final Customer customerSaved = entityManager.find(Customer.class, shoppingCarts.getFirst().getCustomer().getUsername());

        final ShoppingCart firstShoppingCartSaved = entityManager.find(ShoppingCart.class, shoppingCarts.getFirst().getId());
        final ShoppingCart secondShoppingCartSaved = entityManager.find(ShoppingCart.class, shoppingCarts.getLast().getId());
        assertNull(customerSaved);
        assertNotNull(firstShoppingCartSaved);
        assertNotNull(secondShoppingCartSaved);
    }

    private ShoppingCart shoppingCartDB() {
        final ShoppingCart shoppingCart = new ShoppingCart();
        final ShoppingCartLine shoppingCartLine =
                ShoppingCartLine
                    .builder()
                    .quantity(3)
                    .price(3 * 11.45f)
                    .build();
        shoppingCart.getShoppingCartLines().add(shoppingCartLine);
        shoppingCart.setTotalPrice(shoppingCartLine.getPrice());

        entityManager.persist(shoppingCart);
        entityManager.flush();

        assertThat(shoppingCart.getId()).isGreaterThan(0);

        return shoppingCart;
    }

    @Test
    public void test_findByCustomerUsernameAndOrderNumberIsNull() {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findByCustomerUsernameAndOrderNumberIsNull(tom.getUsername());
        logger.info(shoppingCarts.toString());
        shoppingCarts.forEach(shoppingCart -> {
            assertEquals(tom.getUsername(), shoppingCart.getCustomer().getUsername());
            assertNull(shoppingCart.getOrderNumber());
        });
        assertEquals(1, shoppingCarts.size());
    }

    @Test
    public void test_findByCustomerUsernameAndOrderNumberIsNotNull() {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findByCustomerUsernameAndOrderNumberIsNotNull(tom.getUsername());
        logger.info(shoppingCarts.toString());
        shoppingCarts.forEach(shoppingCart -> {
            assertEquals(tom.getUsername(), shoppingCart.getCustomer().getUsername());
            assertNotNull(shoppingCart.getOrderNumber());
        });
        assertEquals(2, shoppingCarts.size());
    }

    private List<ShoppingCart> shoppingCartData() {
        final List<Bottle> bottles = bottleRepository.findAll();
        assertThat(bottles).isNotNull();
        assertFalse(bottles.isEmpty());
        assertThat(bottles.size()).isEqualTo(5);

        final List<ShoppingCart> shoppingCarts = new ArrayList<>();

        final ShoppingCart shoppingCart1 = new ShoppingCart();

        Address address = Address.builder()
                .street("2 rue Georges Perros")
                .zipCode("29000")
                .city("Quimper")
                .build();

        Customer customer = Customer.builder()
                .username("natalieportman@email.fr")
                .password("MarsAttacks!")
                .lastname("Portman")
                .firstname("Natalie")
                .address(address)
                .build();

        int quantity1 = 3;
        final Bottle bottle1 = bottles.get(0);
        final ShoppingCartLine shoppingCartLine1 = ShoppingCartLine.builder()
                .bottle(bottle1)
                .quantity(quantity1)
                .price(quantity1 * bottle1.getPrice())
                .build();
        shoppingCart1.getShoppingCartLines().add(shoppingCartLine1);
        shoppingCart1.setTotalPrice(shoppingCartLine1.getPrice());
        shoppingCart1.setCustomer(customer);
        shoppingCarts.add(shoppingCart1);

        final ShoppingCart shoppingCart2 = new ShoppingCart();
        int quantity2 = 10;
        final Bottle bottle2 = bottles.get(1);
        final ShoppingCartLine shoppingCartLine2 = ShoppingCartLine.builder()
                .bottle(bottle2)
                .quantity(quantity2)
                .price(quantity2 * bottle2.getPrice())
                .build();
        shoppingCart2.getShoppingCartLines().add(shoppingCartLine2);
        shoppingCart2.setTotalPrice(shoppingCartLine2.getPrice());
        shoppingCart2.setCustomer(customer);
        shoppingCarts.add(shoppingCart2);

        return shoppingCarts;
    }

    private void loadDataBottles() {
        final Color red = Color.builder()
                .name("Rouge")
                .build();

        white = Color.builder()
                .name("Blanc")
                .build();

        final Color rose = Color.builder()
                .name("Rosé")
                .build();

        entityManager.persist(red);
        entityManager.persist(white);
        entityManager.persist(rose);
        entityManager.flush();

        final Region grandEst = Region.builder()
                .name("Grand Est")
                .build();

        paysDeLaLoire = Region.builder()
                .name("Pays de la Loire")
                .build();

        final Region nouvelleAquitaine = Region.builder()
                .name("Nouvelle Aquitaine")
                .build();

        entityManager.persist(grandEst);
        entityManager.persist(paysDeLaLoire);
        entityManager.persist(nouvelleAquitaine);
        entityManager.flush();

        bottles = new ArrayList<>();
        bottles.add(Bottle.builder()
                .name("Blanc du DOMAINE ENI Ecole")
                .vintage("2022")
                .price(23.95f)
                .quantity(1298)
                .region(paysDeLaLoire)
                .color(white)
                .build());
        bottles.add(Bottle
                .builder()
                .name("Rouge du DOMAINE ENI Ecole")
                .vintage("2018")
                .price(11.45f)
                .quantity(987)
                .region(paysDeLaLoire)
                .color(red)
                .build());
        bottles.add(Bottle
                .builder()
                .name("Blanc du DOMAINE ENI Service")
                .vintage("2022")
                .price(34)
                .isSparkling(true)
                .quantity(111)
                .region(grandEst)
                .color(white)
                .build());
        bottles.add(Bottle
                .builder()
                .name("Rouge du DOMAINE ENI Service")
                .vintage("2012")
                .price(8.15f)
                .quantity(344)
                .region(paysDeLaLoire)
                .color(red)
                .build());
        bottles.add(Bottle
                .builder()
                .name("Rosé du DOMAINE ENI")
                .vintage("2020")
                .price(33)
                .quantity(1987)
                .region(nouvelleAquitaine)
                .color(rose)
                .build());

        bottles.forEach(e -> {
            entityManager.persist(e);
            // Vérification de l'identifiant
            assertThat(e.getId()).isGreaterThan(0);
        });
        entityManager.flush();

    }

    private void loadDataShoppingCartsCustomers() {
        final Bottle bottle1 = bottles.get(0);
        final Bottle bottle2 = bottles.get(1);
        final Bottle bottle3 = bottles.get(2);

        final List<ShoppingCart> shoppingCarts = new ArrayList<>();
        final ShoppingCart shoppingCart1 = new ShoppingCart();
        final ShoppingCartLine shoppingCartLine1 = ShoppingCartLine.builder()
                .bottle(bottle2)
                .quantity(3)
                .price(3 * bottle2.getPrice())
                .build();
        shoppingCart1.getShoppingCartLines().add(shoppingCartLine1);
        shoppingCart1.setTotalPrice(shoppingCartLine1.getPrice());
        shoppingCarts.add(shoppingCart1);

        final ShoppingCart shoppingCart2 = new ShoppingCart();
        final ShoppingCartLine shoppingCartLine2 = ShoppingCartLine.builder()
                .bottle(bottle1)
                .quantity(10)
                .price(10 * bottle1.getPrice())
                .build();
        shoppingCart2.getShoppingCartLines().add(shoppingCartLine2);
        shoppingCart2.setTotalPrice(shoppingCartLine2.getPrice());
        shoppingCarts.add(shoppingCart2);

        final ShoppingCart shoppingCart3 = new ShoppingCart();
        final ShoppingCartLine shoppingCartLine3 = ShoppingCartLine.builder()
                .bottle(bottle3)
                .quantity(4)
                .price(3 * bottle3.getPrice())
                .build();
        shoppingCart3.getShoppingCartLines().add(shoppingCartLine3);
        shoppingCart3.setTotalPrice(shoppingCartLine3.getPrice());
        shoppingCarts.add(shoppingCart3);

        Address address = Address.builder()
                .street("2 rue Georges Perros")
                .zipCode("29000")
                .city("Quimper")
                .build();

        tom = Customer.builder()
                .username("tomhanks@email.fr")
                .password("ForrestGump")
                .firstname("Hanks")
                .lastname("Tom")
                .address(address)
                .build();

        shoppingCarts.forEach(shoppingCart -> {
            shoppingCart.setCustomer(tom);
        });

        // Contexte de la DB
        shoppingCarts.forEach(shoppingCart -> {
            entityManager.persist(shoppingCart);
            assertThat(shoppingCart.getId()).isGreaterThan(0);
        });
        entityManager.flush();

        // Indiquer les ShoppingCart de ce Client qui ont été commandés
        shoppingCart1.setPaid(true);
        shoppingCart1.setOrderNumber(tom.getUsername() + "_" + shoppingCart1.getId());

        shoppingCart3.setPaid(true);
        shoppingCart3.setOrderNumber(tom.getUsername() + "_" + shoppingCart3.getId());

        entityManager.merge(shoppingCart1);
        entityManager.merge(shoppingCart3);
        entityManager.flush();
    }
}
