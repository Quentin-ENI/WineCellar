package com.eni.winecellar.repository;

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
public class TestBottleRepository {

    private static final Logger logger = LoggerFactory.getLogger(TestBottleRepository.class);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    BottleRepository bottleRepository;

    @Autowired
    ColorRepository colorRepository;

    @Autowired
    RegionRepository regionRepository;

    Color red;
    Color white;
    Color rose;

    Region grandEst;
    Region paysDeLaLoire;
    Region nouvelleAquitaine;

    List<Bottle> bottles;

    @BeforeEach
    public void initializationDB() {
        red = Color.builder()
                .name("Rouge")
                .build();

        white = Color.builder()
                .name("Blanc")
                .build();

        rose = Color.builder()
                .name("Rosé")
                .build();

        colorRepository.save(red);
        colorRepository.save(white);
        colorRepository.save(rose);

        grandEst = Region.builder()
                        .name("Grand Est")
                        .build();

        paysDeLaLoire = Region.builder()
                        .name("Pays de la Loire")
                        .build();

        nouvelleAquitaine = Region.builder()
                        .name("Nouvelle Aquitaine")
                        .build();

        regionRepository.save(grandEst);
        regionRepository.save(paysDeLaLoire);
        regionRepository.save(nouvelleAquitaine);

        bottles = new ArrayList<>();
        bottles.add(Bottle.builder()
                .name("Blanc du DOMAINE ENI Ecole")
                .vintage("2022")
                .price(23.95f)
                .quantity(1298)
                .region(paysDeLaLoire)
                .color(white)
                .build());
        bottles.add(Bottle.builder()
                .name("Rouge du DOMAINE ENI Ecole")
                .vintage("2018")
                .price(11.45f)
                .quantity(987)
                .region(paysDeLaLoire)
                .color(red)
                .build());
        bottles.add(Bottle.builder()
                .name("Blanc du DOMAINE ENI Service")
                .vintage("2022")
                .price(34)
                .isSparkling(true)
                .quantity(111)
                .region(grandEst)
                .color(white)
                .build());
        bottles.add(Bottle.builder()
                .name("Rouge du DOMAINE ENI Service")
                .vintage("2012")
                .price(8.15f)
                .quantity(344)
                .region(paysDeLaLoire)
                .color(red)
                .build());
        bottles.add(Bottle.builder()
                .name("Rosé du DOMAINE ENI Service")
                .vintage("2020")
                .price(33)
                .quantity(1987)
                .region(nouvelleAquitaine)
                .color(rose)
                .build());

        bottles.forEach(bottle -> {
            entityManager.persist(bottle);
            // Vérification de l'identifiant
            assertThat(bottle.getId()).isGreaterThan(0);
        });
        entityManager.flush();
    }

    @Test
    public void test_save() {
        Bottle bottle = bottleData().getFirst();

        bottleRepository.save(bottle);
        Bottle bottleSaved = entityManager.find(Bottle.class, bottle.getId());
        assertEquals(bottleSaved, bottle);
        logger.info(bottle.toString());
    }

    @Test
    public void test_save_bottles_regions_colors() {
        List<Bottle> bottles = bottleData();
        bottleRepository.saveAll(bottles);
        bottles.forEach(bottle -> {
            Bottle bottleSaved = entityManager.find(Bottle.class, bottle.getId());
            List<Integer> colorIds = List.of(red.getId(), white.getId(), rose.getId());
            assertTrue(colorIds.contains(bottleSaved.getColor().getId()));
            List<Integer> regionIds = List.of(paysDeLaLoire.getId(), nouvelleAquitaine.getId(), grandEst.getId());
            assertTrue(regionIds.contains(bottleSaved.getRegion().getId()));
        });
    }

    @Test
    public void test_delete() {
        Bottle bottle = bottleData().getFirst();
        entityManager.persist(bottle);
        entityManager.flush();

        Integer bottleId = bottle.getId();
        Integer colorId = bottle.getColor().getId();
        Integer regionId = bottle.getRegion().getId();

        bottleRepository.delete(bottle);
        Bottle bottleDeleted = entityManager.find(Bottle.class, bottleId);
        assertNull(bottleDeleted);
        Color color = entityManager.find(Color.class, colorId);
        assertNotNull(color);
        Region region = entityManager.find(Region.class, regionId);
        assertNotNull(region);
    }

    @Test
    public void test_findByRegion() {
        final List<Bottle> bottles = bottleRepository.findByRegion(paysDeLaLoire.getId());
        assertEquals(3, bottles.size());
        bottles.forEach(bottle -> assertEquals(paysDeLaLoire.getId(), bottle.getRegion().getId()));
    }

    @Test
    public void test_findByColor() {
        final List<Bottle> bottles = bottleRepository.findByColor(white.getId());
        assertEquals(2, bottles.size());
        bottles.forEach(bottle -> assertEquals(white.getId(), bottle.getColor().getId()));
    }

    private List<Bottle> bottleData() {
        List<Bottle> bottles = new ArrayList<>();
        bottles.add(Bottle.builder()
                .name("Blanc du DOMAINE ENI Editions")
                .vintage("2022")
                .price(23.95f)
                .quantity(1298)
                .region(paysDeLaLoire)
                .color(white)
                .build());
        bottles.add(Bottle.builder()
                .name("Rouge du DOMAINE ENI Editions")
                .vintage("2018")
                .price(11.45f)
                .quantity(987)
                .region(paysDeLaLoire)
                .color(red)
                .build());
        bottles.add(Bottle.builder()
                .name("Blanc du DOMAINE")
                .vintage("2022")
                .price(34)
                .isSparkling(true)
                .quantity(111)
                .region(grandEst)
                .color(white)
                .build());
        bottles.add(Bottle.builder()
                .name("Rouge du DOMAINE")
                .vintage("2012")
                .price(8.15f)
                .quantity(344)
                .region(paysDeLaLoire)
                .color(red)
                .build());
        bottles.add(Bottle
                .builder()
                .name("Rosé du DOMAINE")
                .vintage("2020")
                .price(33)
                .quantity(1987)
                .region(nouvelleAquitaine)
                .color(rose)
                .build());
        return bottles;
    }
}
