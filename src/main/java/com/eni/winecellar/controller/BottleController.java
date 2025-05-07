package com.eni.winecellar.controller;

import com.eni.winecellar.bll.BottleService;
import com.eni.winecellar.bo.wine.Bottle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    public ResponseEntity<ApiResponse<List<Bottle>>> get(
            Locale locale
    ) {
        List<Bottle> bottles = bottleService.loadBottles();

        if (bottles.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            String message = messageSource.getMessage("bottle.get.list.successful", null, locale);
            return ResponseEntity.ok().body(
                    new ApiResponse<>(
                            ApiResponse.IS_SUCCESSFUL,
                            message,
                            bottles
                    )
            );
        }
    }

    @Operation(summary = "Get bottle by id", description = "Get bottle by id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{bottle_id}")
    public ResponseEntity<ApiResponse<Bottle>> getById(
            @PathVariable(name="bottle_id", required=true) String bottleId,
            Locale locale
    ) {
        try {
            final Bottle bottle = bottleService.loadBottleById(Integer.parseInt(bottleId));
            String message = messageSource.getMessage("bottle.get.by-id.successful", null, locale);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            ApiResponse.IS_SUCCESSFUL,
                            message,
                            bottle
                    )
            );
        } catch (NumberFormatException e) {
            String errorMessage = messageSource.getMessage("bottle.validation.id.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ApiResponse<>(
                            ApiResponse.NOT_SUCCESSFUL,
                            errorMessage,
                            null
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get bottles by region", description = "Get bottles by region")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/region/{region_id}")
    public ResponseEntity<ApiResponse<List<Bottle>>> getByRegionId(
            @PathVariable(name="region_id", required=true) String regionId,
            Locale locale
    ) {
        try {
            List<Bottle> bottles = bottleService.loadBottlesByRegion(Integer.parseInt(regionId));
            if (bottles.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                String message = messageSource.getMessage("bottle.get.list.successful", null, locale);
                return ResponseEntity.ok().body(
                        new ApiResponse<>(
                                ApiResponse.IS_SUCCESSFUL,
                                message,
                                bottles
                        )
                );
            }
        } catch(NumberFormatException e) {
            String errorMessage = messageSource.getMessage("region.validation.id.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ApiResponse<>(
                            ApiResponse.NOT_SUCCESSFUL,
                            errorMessage,
                            null
                    )
            );
        } catch(RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get bottles by color", description = "Get bottles by color")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/color/{color_id}")
    public ResponseEntity<ApiResponse<List<Bottle>>> getByColorId(
            @PathVariable(name="color_id", required=true) String colorId,
            Locale locale
    ) {
        try {
            List<Bottle> bottles = bottleService.loadBottlesByColor(Integer.parseInt(colorId));
            if (bottles.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                String message = messageSource.getMessage("bottle.get.list.successful", null, locale);
                return ResponseEntity.ok().body(
                        new ApiResponse<>(
                                ApiResponse.IS_SUCCESSFUL,
                                message,
                                bottles
                        )
                );
            }
        } catch(NumberFormatException e) {
            String errorMessage = messageSource.getMessage("color.validation.id.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ApiResponse<>(
                            ApiResponse.NOT_SUCCESSFUL,
                            errorMessage,
                            null
                    )
            );
        } catch(RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Post bottle", description = "Post bottle")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<ApiResponse<Bottle>> save(
            @Valid @RequestBody Bottle bottle,
            Locale locale
    ) {
        try {
            bottle = bottleService.add(bottle);
            String message = messageSource.getMessage("bottle.save.successful", null, locale);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            ApiResponse.IS_SUCCESSFUL,
                            message,
                            bottle
                    )
            );
        } catch(RuntimeException e) {
            String errorMessage = messageSource.getMessage("bottle.validation.body.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ApiResponse<>(
                            ApiResponse.NOT_SUCCESSFUL,
                            errorMessage,
                            null
                    )
            );
        }
    }

    @Operation(summary = "Put bottle", description = "Put bottle")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping
    public ResponseEntity<ApiResponse<Bottle>> update(
            @Valid @RequestBody Bottle bottle,
            Locale locale
    ) {
        try {
            if (bottle == null || bottle.getId() == null || bottle.getId() <= 0) {
                String errorMessage = messageSource.getMessage("bottle.validation.body.not-valid", null, locale);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        new ApiResponse<>(
                                ApiResponse.NOT_SUCCESSFUL,
                                errorMessage,
                                bottle
                        )
                );
            }
            bottle = bottleService.add(bottle);
            String message = messageSource.getMessage("bottle.save.successful", null, locale);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            ApiResponse.IS_SUCCESSFUL,
                            message,
                            bottle
                    )
            );
        } catch(RuntimeException e) {
            String errorMessage = messageSource.getMessage("bottle.validation.body.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ApiResponse<>(
                            ApiResponse.NOT_SUCCESSFUL,
                            errorMessage,
                            bottle
                    )
            );
        }
    }

    @Operation(summary = "Delete bottle", description = "Delete bottle")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{bottle_id}")
    public ResponseEntity<ApiResponse<Integer>> delete(
            @PathVariable(name="bottle_id", required=true) String bottleId,
            Locale locale
    ) {
        Integer integerBottleId = Integer.parseInt(bottleId);
        try {
            bottleService.delete(integerBottleId);
            String message = messageSource.getMessage("bottle.delete.successful", new String[]{bottleId}, locale);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            ApiResponse.IS_SUCCESSFUL,
                            message,
                            integerBottleId
                    )
            );
        } catch (NumberFormatException e) {
            String errorMessage = messageSource.getMessage("bottle.validation.id.not-valid", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ApiResponse<>(
                            ApiResponse.NOT_SUCCESSFUL,
                            errorMessage,
                            integerBottleId
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
