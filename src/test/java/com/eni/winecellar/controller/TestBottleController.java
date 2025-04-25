package com.eni.winecellar.controller;

import com.eni.winecellar.bll.BottleService;
import com.eni.winecellar.bo.wine.Bottle;
import com.eni.winecellar.bo.wine.Color;
import com.eni.winecellar.bo.wine.Region;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TestBottleController {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BottleService bottleService;

    @Test
    void test_get_whenBottlesIsNotEmpty_thenReturnOK() throws Exception {
        List<Bottle> bottles = bottleData();

        Mockito.when(bottleService.loadBottles()).thenReturn(bottles);

        mockMvc.perform(get("/winecellar/bottles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(bottles.size()))
                .andExpect(jsonPath("$[0].name").value("Blanc du DOMAINE ENI Editions"))
                .andExpect(jsonPath("$[0].vintage").value("2022"))
                .andExpect(jsonPath("$[0].price").value(23.95))
                .andExpect(jsonPath("$[0].quantity").value(1298))
                .andExpect(jsonPath("$[0].region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("$[0].color.name").value("Blanc"));
    }

    @Test
    void test_get_whenBottlesIsEmpty_thenReturnNoContent() throws Exception {
        List<Bottle> bottles = List.of();

        Mockito.when(bottleService.loadBottles()).thenReturn(bottles);

        mockMvc.perform(get("/winecellar/bottles"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void test_getById_whenBottleExistsAndAnonymousUser_thenReturn401() throws Exception {
        Bottle bottle = bottleData().get(0);
        int bottleId = 1;
        Mockito.when(bottleService.loadBottleById(bottleId)).thenReturn(bottle);

        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_getById_whenBottleExistsAndCustomerUser_thenReturnOk() throws Exception {
        Bottle bottle = bottleData().get(0);
        int bottleId = 1;
        Mockito.when(bottleService.loadBottleById(bottleId)).thenReturn(bottle);

        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Blanc du DOMAINE ENI Editions"))
                .andExpect(jsonPath("$.vintage").value("2022"))
                .andExpect(jsonPath("$.price").value(23.95))
                .andExpect(jsonPath("$.quantity").value(1298))
                .andExpect(jsonPath("$.region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("$.color.name").value("Blanc"));
    }

    @Test
    @WithMockUser(roles={"OWNER"})
    void test_getById_whenBottleExistsAndOwnerUser_thenReturnOk() throws Exception {
        Bottle bottle = bottleData().get(0);
        int bottleId = 1;
        Mockito.when(bottleService.loadBottleById(bottleId)).thenReturn(bottle);

        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Blanc du DOMAINE ENI Editions"))
                .andExpect(jsonPath("$.vintage").value("2022"))
                .andExpect(jsonPath("$.price").value(23.95))
                .andExpect(jsonPath("$.quantity").value(1298))
                .andExpect(jsonPath("$.region.name").value("Pays de la Loire"))
                .andExpect(jsonPath("$.color.name").value("Blanc"));
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_getById_whenBottleDoesNotExist_thenReturnNotFound() throws Exception {
        int bottleId = 1;
        Mockito.when(bottleService.loadBottleById(bottleId)).thenThrow(new RuntimeException("Bottle not found"));

        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", bottleId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles={"CUSTOMER"})
    void test_getById_whenBottleFormatParameterIsNotAccepted_thenReturnNotAcceptable() throws Exception {
        String expectedMessage = "Bottle id is not valid";
        mockMvc.perform(get("/winecellar/bottles/{bottle_id}", "azerty"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(expectedMessage));
    }


    // DATA
    List<Bottle> bottleData() {
        Region paysDeLaLoire = Region.builder()
                .name("Pays de la Loire")
                .build();

        Color red = Color.builder()
                .name("Rouge")
                .build();

        Color white = Color.builder()
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

        return bottles;
    }
}
