package com.eni.winecellar.controller;

import com.eni.winecellar.bll.BottleService;
import com.eni.winecellar.bo.wine.Bottle;
import com.eni.winecellar.bo.wine.Color;
import com.eni.winecellar.bo.wine.Region;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TestBottleController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BottleService bottleService;

    // HTTP : GET /bottles
    @Test
    void test_get_whenBottlesIsNotEmpty_thenReturn200() throws Exception {
        List<Bottle> bottles = bottleListData();

        Mockito.when(bottleService.loadBottles()).thenReturn(bottles);

        mockMvc.perform(get("/winecellar/bottles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value("Bottles retrieved successfully"))
                .andExpect(jsonPath("data.length()").value(bottles.size()))
                .andExpect(jsonPath("data[0].name").value("Blanc du DOMAINE ENI Editions"))
                .andExpect(jsonPath("data[0].vintage").value("2022"))
                .andExpect(jsonPath("data[0].price").value(23.95))
                .andExpect(jsonPath("data[0].quantity").value(1298))
                .andExpect(jsonPath("data[0].region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("data[0].color.name").value("Blanc"));
    }

    @Test
    void test_get_whenBottlesIsEmpty_thenReturn204() throws Exception {
        List<Bottle> bottles = List.of();

        Mockito.when(bottleService.loadBottles()).thenReturn(bottles);

        mockMvc.perform(get("/winecellar/bottles"))
                .andExpect(status().isNoContent());
    }

    // HTTP : GET /bottles/{bottle_id}
    @Test
    @WithAnonymousUser
    void test_getById_whenBottleIsPresentAndUserIsAnonymous_thenReturn403() throws Exception {
        Bottle bottle = bottleListData().get(0);
        int bottleId = 1;
        Mockito.when(bottleService.loadBottleById(bottleId)).thenReturn(bottle);

        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_getById_whenBottleIsPresentAndUserIsCustomer_thenReturn200() throws Exception {
        Bottle bottle = bottleListData().get(0);
        int bottleId = 1;
        Mockito.when(bottleService.loadBottleById(bottleId)).thenReturn(bottle);

        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value("Bottle retrieved succesfully"))
                .andExpect(jsonPath("data.name").value("Blanc du DOMAINE ENI Editions"))
                .andExpect(jsonPath("data.vintage").value("2022"))
                .andExpect(jsonPath("data.price").value(23.95))
                .andExpect(jsonPath("data.quantity").value(1298))
                .andExpect(jsonPath("data.region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("data.color.name").value("Blanc"));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getById_whenBottleIsPresentAndUserIsOwner_thenReturn200() throws Exception {
        Bottle bottle = bottleListData().getFirst();
        int bottleId = 1;
        Mockito.when(bottleService.loadBottleById(bottleId)).thenReturn(bottle);

        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value("Bottle retrieved succesfully"))
                .andExpect(jsonPath("data.name").value("Blanc du DOMAINE ENI Editions"))
                .andExpect(jsonPath("data.vintage").value("2022"))
                .andExpect(jsonPath("data.price").value(23.95))
                .andExpect(jsonPath("data.quantity").value(1298))
                .andExpect(jsonPath("data.region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("data.color.name").value("Blanc"));
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_getById_whenBottleIsNotPresent_thenReturn404() throws Exception {
        int bottleId = 1;
        Mockito.when(bottleService.loadBottleById(bottleId)).thenThrow(new RuntimeException("Bottle not found"));

        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_getById_whenFormatParameterIsNotAccepted_thenReturn406() throws Exception {
        String expectedMessage = "Bottle id is not valid";
        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", "azerty"))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage));
    }

    // HTTP : GET /bottles/region/{region_id}
    @Test
    @WithAnonymousUser
    void test_getByRegionId_whenBottlesIsNotEmptyAndUserIsAnonymous_thenReturn403() throws Exception {
        int regionId = 1;
        List<Bottle> bottles = bottleListData()
                .stream()
                .filter(bottle -> bottle.getRegion().getId() == regionId)
                .toList();
        Mockito.when(bottleService.loadBottlesByRegion(regionId)).thenReturn(bottles);

        mockMvc.perform(get("/winecellar/bottles/region/{region_id}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_getByRegionId_whenBottlesExistsAndUserIsCustomer_thenReturn200() throws Exception {
        int regionId = 1;
        List<Bottle> bottles = bottleListData()
                .stream()
                .filter(bottle -> bottle.getRegion().getId() == regionId)
                .toList();
        Mockito.when(bottleService.loadBottlesByRegion(regionId)).thenReturn(bottles);

        String expectedMessage = "Bottles retrieved successfully";

        mockMvc.perform(get("/winecellar/bottles/region/{region_id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage))
                .andExpect(jsonPath("data.length()").value(bottles.size()))
                .andExpect(jsonPath("data[0].name").value("Blanc du DOMAINE ENI Editions"))
                .andExpect(jsonPath("data[0].vintage").value("2022"))
                .andExpect(jsonPath("data[0].price").value(23.95))
                .andExpect(jsonPath("data[0].quantity").value(1298))
                .andExpect(jsonPath("data[0].region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("data[0].color.name").value("Blanc"));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getByRegionId_whenBottlesExistsAndUserIsOwner_thenReturn200() throws Exception {
        int regionId = 1;
        List<Bottle> bottles = bottleListData()
                .stream()
                .filter(bottle -> bottle.getRegion().getId() == regionId)
                .toList();
        Mockito.when(bottleService.loadBottlesByRegion(regionId)).thenReturn(bottles);

        String expectedMessage = "Bottles retrieved successfully";

        mockMvc.perform(get("/winecellar/bottles/region/{region_id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage))
                .andExpect(jsonPath("data.length()").value(bottles.size()))
                .andExpect(jsonPath("data[0].name").value("Blanc du DOMAINE ENI Editions"))
                .andExpect(jsonPath("data[0].vintage").value("2022"))
                .andExpect(jsonPath("data[0].price").value(23.95))
                .andExpect(jsonPath("data[0].quantity").value(1298))
                .andExpect(jsonPath("data[0].region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("data[0].color.name").value("Blanc"));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getByRegionId_whenBottlesNotExist_thenReturn204() throws Exception {
        int regionId = 1;
        List<Bottle> bottles = List.of();
        Mockito.when(bottleService.loadBottlesByRegion(regionId)).thenReturn(bottles);

        mockMvc.perform(get("/winecellar/bottles/region/{region_id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getByRegionId_whenFormatParameterIsNotAccepted_thenReturn406() throws Exception {
        String expectedMessage = "Region id is not valid";
        mockMvc.perform(get("/winecellar/bottles/region/{region_id}", "azerty"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getByRegionId_whenRegionDoesNotExist_thenReturn404() throws Exception {
        int regionId = 1;
        Mockito.when(bottleService.loadBottlesByRegion(regionId)).thenThrow(new RuntimeException("Region not found"));

        mockMvc.perform(get("/winecellar/bottles/region/{region_id}", regionId))
                .andExpect(status().isNotFound());
    }

    // HTTP : GET /bottles/color/{color_id}
    @Test
    @WithAnonymousUser
    void test_getByColorId_whenBottlesIsNotEmptyAndUserIsAnonymous_thenReturn401() throws Exception {
        int colorId = 1;
        List<Bottle> bottles = bottleListData()
                .stream()
                .filter(bottle -> bottle.getColor().getId() == colorId)
                .toList();
        Mockito.when(bottleService.loadBottlesByColor(colorId)).thenReturn(bottles);

        mockMvc.perform(get("/winecellar/bottles/color/{color_id}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_getByColorId_whenBottlesExistsAndUserIsCustomer_thenReturn200() throws Exception {
        int colorId = 1;
        List<Bottle> bottles = bottleListData()
                .stream()
                .filter(bottle -> bottle.getColor().getId() == colorId)
                .toList();
        Mockito.when(bottleService.loadBottlesByColor(colorId)).thenReturn(bottles);

        String expectedMessage = "Bottles retrieved successfully";

        mockMvc.perform(get("/winecellar/bottles/color/{color_id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage))
                .andExpect(jsonPath("data.length()").value(bottles.size()))
                .andExpect(jsonPath("data[0].name").value("Rouge du DOMAINE ENI Editions"))
                .andExpect(jsonPath("data[0].vintage").value("2018"))
                .andExpect(jsonPath("data[0].price").value(11.45))
                .andExpect(jsonPath("data[0].quantity").value(987))
                .andExpect(jsonPath("data[0].region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("data[0].color.name").value("Rouge"));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getByColorId_whenBottlesExistsAndUserIsOwner_thenReturn200() throws Exception {
        int colorId = 1;
        List<Bottle> bottles = bottleListData()
                .stream()
                .filter(bottle -> bottle.getColor().getId() == colorId)
                .toList();
        Mockito.when(bottleService.loadBottlesByColor(colorId)).thenReturn(bottles);

        String expectedMessage = "Bottles retrieved successfully";

        mockMvc.perform(get("/winecellar/bottles/color/{color_id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage))
                .andExpect(jsonPath("data.length()").value(bottles.size()))
                .andExpect(jsonPath("data[0].name").value("Rouge du DOMAINE ENI Editions"))
                .andExpect(jsonPath("data[0].vintage").value("2018"))
                .andExpect(jsonPath("data[0].price").value(11.45))
                .andExpect(jsonPath("data[0].quantity").value(987))
                .andExpect(jsonPath("data[0].region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("data[0].color.name").value("Rouge"));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getByColorId_whenBottlesNotExist_thenReturn204() throws Exception {
        int colorId = 1;
        List<Bottle> bottles = List.of();
        Mockito.when(bottleService.loadBottlesByColor(colorId)).thenReturn(bottles);

        mockMvc.perform(get("/winecellar/bottles/color/{color_id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getByColorId_whenFormatParameterIsNotAccepted_thenReturnNotAcceptable() throws Exception {
        String expectedMessage = "Color id is not valid";
        mockMvc.perform(get("/winecellar/bottles/color/{color_id}", "azerty"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getByColorId_whenColorDoesNotExist_thenReturn404() throws Exception {
        int colorId = 1;
        Mockito.when(bottleService.loadBottlesByColor(colorId)).thenThrow(new RuntimeException("Color not found"));

        mockMvc.perform(get("/winecellar/bottles/color/{color_id}", colorId))
                .andExpect(status().isNotFound());
    }

    // HTTP : POST /bottles
    @Test
    @WithAnonymousUser
    void test_save_whenBodyValidUserIsAnonymous_thenReturn403() throws Exception {
        Bottle bottle = bottleData();

        mockMvc.perform(
                    post("/winecellar/bottles", bottle)
                        .content(objectMapper.writeValueAsString(bottle))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_save_whenBodyValidUserIsCustomer_thenReturn403() throws Exception {
        Bottle bottle = bottleData();

        mockMvc.perform(
                post("/winecellar/bottles", bottle)
                        .content(objectMapper.writeValueAsString(bottle))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_save_whenBodyValidUserIsOwner_thenReturn200() throws Exception {
        Bottle bottle = bottleData();
        Bottle bottleDB = bottleData();
        bottleDB.setId(1);
        Mockito.when(bottleService.add(bottle)).thenReturn(bottleDB);

        String expectedMessage = "Bottle saved successfully";

        mockMvc.perform(
                    post("/winecellar/bottles", bottle)
                            .content(objectMapper.writeValueAsString(bottle))
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage))
                .andExpect(jsonPath("data.id").value(1))
                .andExpect(jsonPath("data.name").value("Rouge du DOMAINE ENI Ecole"))
                .andExpect(jsonPath("data.vintage").value("2018"))
                .andExpect(jsonPath("data.price").value(11.45))
                .andExpect(jsonPath("data.quantity").value(987))
                .andExpect(jsonPath("data.region.name").value("Nouvelle Aquitaine"))
                .andExpect(jsonPath("data.color.name").value("Rouge"));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_save_whenBodyNotValidAndUserIsOwner_thenReturn406() throws Exception {
        Bottle bottle = bottleData();
        bottle.setName(null);

        String expectedMessage = "Error(s) : Name is required\n";
        mockMvc.perform(
                    post("/winecellar/bottles", bottle)
                        .content(objectMapper.writeValueAsString(bottle))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_save_whenSaveThrowsExceptionAndUserIsOwner_thenReturn406() throws Exception {
        Bottle bottle = bottleData();
        Mockito.when(bottleService.add(bottle)).thenThrow(new RuntimeException());

        String expectedMessage = "Body is not valid";
        mockMvc.perform(
                        post("/winecellar/bottles", bottle)
                                .content(objectMapper.writeValueAsString(bottle))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage));
    }

    // HTTP : PUT /bottles
    @Test
    @WithAnonymousUser
    void test_update_whenBodyValidUserIsAnonymous_thenReturn403() throws Exception {
        Bottle bottle = bottleData();

        mockMvc.perform(
                        put("/winecellar/bottles", bottle)
                                .content(objectMapper.writeValueAsString(bottle))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_update_whenBodyValidUserIsCustomer_thenReturn403() throws Exception {
        Bottle bottle = bottleData();

        mockMvc.perform(
                        put("/winecellar/bottles", bottle)
                                .content(objectMapper.writeValueAsString(bottle))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_update_whenBodyValidUserIsOwner_thenReturn200() throws Exception {
        Bottle bottle = bottleData();
        bottle.setId(1);
        Bottle bottleDB = bottleData();
        bottleDB.setId(1);
        Mockito.when(bottleService.add(bottle)).thenReturn(bottleDB);

        String expectedMessage = "Bottle saved successfully";

        mockMvc.perform(
                        put("/winecellar/bottles", bottle)
                                .content(objectMapper.writeValueAsString(bottle))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.IS_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage))
                .andExpect(jsonPath("data.id").value(1))
                .andExpect(jsonPath("data.name").value("Rouge du DOMAINE ENI Ecole"))
                .andExpect(jsonPath("data.vintage").value("2018"))
                .andExpect(jsonPath("data.price").value(11.45))
                .andExpect(jsonPath("data.quantity").value(987))
                .andExpect(jsonPath("data.region.name").value("Nouvelle Aquitaine"))
                .andExpect(jsonPath("data.color.name").value("Rouge"));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_update_whenBodyNotValidAndUserIsOwner_thenReturn406() throws Exception {
        Bottle bottle = bottleData();
        bottle.setId(1);
        bottle.setName(null);

        String expectedMessage = "Error(s) : Name is required\n";
        mockMvc.perform(
                        put("/winecellar/bottles", bottle)
                                .content(objectMapper.writeValueAsString(bottle))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_update_whenBottleIdIsNullAndUserIsOwner_thenReturn406() throws Exception {
        Bottle bottle = bottleData();
        bottle.setId(null);

        String expectedMessage = "Body is not valid";
        mockMvc.perform(
                        put("/winecellar/bottles", bottle)
                                .content(objectMapper.writeValueAsString(bottle))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_update_whenSaveThrowsExceptionAndUserIsOwner_thenReturn406() throws Exception {
        Bottle bottle = bottleData();
        Mockito.when(bottleService.add(bottle)).thenThrow(new RuntimeException());

        String expectedMessage = "Body is not valid";
        mockMvc.perform(
                        post("/winecellar/bottles", bottle)
                                .content(objectMapper.writeValueAsString(bottle))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage));
    }

    // HTTP : DELETE /bottles/{bottle_id}
    @Test
    @WithAnonymousUser
    void test_delete_whenUserIsAnonymous_thenReturn403() throws Exception {
        int bottleId = 1;

        mockMvc.perform(delete("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_delete_whenUserIsCustomer_thenReturn403() throws Exception {
        int bottleId = 1;

        mockMvc.perform(delete("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_delete_whenUserIsOwner_thenReturn200() throws Exception {
        Integer bottleId = 1;
        doNothing().when(bottleService).delete(bottleId);

        mockMvc.perform(delete("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_delete_whenThrowsNumberFormatExceptionAndUserIsOwner_thenReturn406() throws Exception {
        Integer bottleId = 1;

        doThrow(NumberFormatException.class).when(bottleService).delete(bottleId);

        String expectedMessage = "Bottle id is not valid";

        mockMvc.perform(delete("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(ApiResponse.NOT_SUCCESSFUL))
                .andExpect(jsonPath("message").value(expectedMessage))
                .andExpect(jsonPath("data").value(bottleId));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_delete_whenThrowsRuntimeExceptionExceptionAndUserIsOwner_thenReturn404() throws Exception {
        Integer bottleId = 1;

        doThrow(RuntimeException.class).when(bottleService).delete(bottleId);

        mockMvc.perform(delete("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isNotFound());
    }

    // DATA
    List<Bottle> bottleListData() {
        Region paysDeLaLoire = Region.builder()
                .id(1)
                .name("Pays de la Loire")
                .build();

        Region nouvelleAquitaine = Region.builder()
                .id(2)
                .name("Nouvelle Aquitaine")
                .build();

        Color red = Color.builder()
                .id(1)
                .name("Rouge")
                .build();

        Color white = Color.builder()
                .id(2)
                .name("Blanc")
                .build();

        List<Bottle> bottles = new ArrayList();

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
                .name("Rouge du DOMAINE ENI Ecole")
                .vintage("2018")
                .price(11.45f)
                .quantity(987)
                .region(nouvelleAquitaine)
                .color(red)
                .build());

        return bottles;
    }

    Bottle bottleData() {
        Region nouvelleAquitaine = Region.builder()
                .id(2)
                .name("Nouvelle Aquitaine")
                .build();

        Color red = Color.builder()
                .id(1)
                .name("Rouge")
                .build();

        return Bottle.builder()
                .name("Rouge du DOMAINE ENI Ecole")
                .vintage("2018")
                .price(11.45f)
                .quantity(987)
                .region(nouvelleAquitaine)
                .color(red)
                .build();

    }
}
