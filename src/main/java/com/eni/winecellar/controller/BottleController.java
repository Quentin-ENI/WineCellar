package com.eni.winecellar.controller;

import com.eni.winecellar.bll.BottleService;
import com.eni.winecellar.bo.wine.Bottle;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@AllArgsConstructor
@RestController
@RequestMapping("/winecellar/bottles")
public class BottleController {
    private BottleService bottleService;

    private MessageSource messageSource;

    @GetMapping
    public ResponseEntity<?> get() {
        List<Bottle> bottles = bottleService.loadBottles();

        if (bottles.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().body(bottles);
        }
    }

    @GetMapping("/{bottle_id}")
    public ResponseEntity<?> getById(
            @PathVariable(name="bottle_id", required=true) String bottleId,
            Locale locale
    ) {
        try {
            final Bottle bottle = bottleService.loadBottleById(Integer.parseInt(bottleId));
            return ResponseEntity.ok(bottle);
        } catch (NumberFormatException e) {
            String errorMessage = messageSource.getMessage("bottle.id.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/region/{region_id}")
    public ResponseEntity<?> getByRegionId(
            @PathVariable(name="region_id", required=true) String regionId,
            Locale locale
    ) {
        try {
            List<Bottle> bottles = bottleService.loadBottlesByRegion(Integer.parseInt(regionId));
            if (bottles.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok().body(bottles);
            }
        } catch(NumberFormatException e) {
            String errorMessage = messageSource.getMessage("region.id.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorMessage);
        } catch(RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/color/{color_id}")
    public ResponseEntity<?> getByColorId(
            @PathVariable(name="color_id", required=true) String colorId,
            Locale locale
    ) {
        try {
            List<Bottle> bottles = bottleService.loadBottlesByColor(Integer.parseInt(colorId));
            if (bottles.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok().body(bottles);
            }
        } catch(NumberFormatException e) {
            String errorMessage = messageSource.getMessage("color.id.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorMessage);
        } catch(RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> save(
            @Valid @RequestBody Bottle bottle
    ) {
        try {
            bottleService.add(bottle);
            return ResponseEntity.ok(bottle);
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> update(
            @Valid @RequestBody Bottle bottle
    ) {
        try {
            if (bottle == null || bottle.getId() == null || bottle.getId() <= 0) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("La bouteille et l'identifiant sont obligatoires");
            }
            bottleService.add(bottle);
            return ResponseEntity.ok(bottle);
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @DeleteMapping("/{bottle_id}")
    public ResponseEntity<?> delete(
            @PathVariable(name="bottle_id", required=true) String bottleId
    ) {
        try {
            bottleService.delete(Integer.parseInt(bottleId));
            return ResponseEntity.ok("Bouteille (" + bottleId + ") est supprimée");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("L'identifiant n'est pas valide");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
