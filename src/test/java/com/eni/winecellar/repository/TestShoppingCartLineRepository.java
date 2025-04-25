package com.eni.winecellar.repository;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
public class TestShoppingCartLineRepository {

    private static final Logger logger = LoggerFactory.getLogger(TestShoppingCartLineRepository.class);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    ShoppingCartLineRepository repository;


    private Bottle bottle;

    @BeforeEach
    public void initializationDB() {
        final Color white = Color.builder()
                .name("Blanc")
                .build();

        entityManager.persist(white);

        final Region paysDeLaLoire = Region.builder()
                .name("Pays de la Loire")
                .build();

        entityManager.persist(paysDeLaLoire);
        entityManager.flush();

        bottle = Bottle.builder()
                .name("DOMAINE ENI Ecole")
                .vintage("2022")
                .price(23.95f)
                .quantity(1298)
                .region(paysDeLaLoire)
                .color(white)
                .build();
        entityManager.persist(bottle);
        entityManager.flush();
    }

    @Test
    public void test_save() {
        int quantity = 4;
        final ShoppingCartLine shoppingCartLine = ShoppingCartLine.builder()
                .quantity(quantity)
                .price(quantity * bottle.getPrice())
                .build();

        // Association OneToMany
        shoppingCartLine.setBottle(bottle);

        // Appel du comportement
        final ShoppingCartLine shoppingCartLineDB = repository.save(shoppingCartLine);
        // Vérification de l'identifiant
        assertThat(shoppingCartLineDB.getId()).isGreaterThan(0);

        // Vérification de l'association
        assertThat(shoppingCartLineDB.getBottle()).isNotNull();
        assertThat(shoppingCartLineDB.getBottle().getId()).isEqualTo(bottle.getId());
        assertThat(shoppingCartLineDB.getPrice()).isEqualTo(quantity * bottle.getPrice());
        logger.info(shoppingCartLineDB.toString());
    }

    @Test
    public void test_delete() {
        int quantity = 4;
        final ShoppingCartLine shoppingCartLine = ShoppingCartLine.builder()
                .quantity(quantity)
                .price(quantity * bottle.getPrice())
                .build();

        // Association OneToMany
        shoppingCartLine.setBottle(bottle);

        // Appel du comportement
        final ShoppingCartLine shoppingCartLineDB = entityManager.persist(shoppingCartLine);
        entityManager.flush();
        // Vérification de l'identifiant
        assertThat(shoppingCartLineDB.getId()).isGreaterThan(0);

        // Appel du comportement
        repository.delete(shoppingCartLineDB);

        // Vérification que l'entité a été supprimée
        final ShoppingCartLine ShoppingCartLineDB2 = entityManager.find(ShoppingCartLine.class, shoppingCartLineDB.getId());
        assertNull(ShoppingCartLineDB2);

        //Vérifier que la bouteille n'a pas été supprimée
        final Bottle bottle = entityManager.find(Bottle.class, this.bottle.getId());
        assertNotNull(this.bottle);
    }
}
