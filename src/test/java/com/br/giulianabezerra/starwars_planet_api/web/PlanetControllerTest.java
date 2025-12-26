package com.br.giulianabezerra.starwars_planet_api.web;

import com.br.giulianabezerra.starwars_planet_api.domain.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objMapper;

    @MockitoBean
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidDate_Returns202Created() throws Exception {
        when(planetService.create(PLANET)).thenReturn(PLANET);

        mockMvc.perform(post("/planets").content(objMapper.writeValueAsBytes(PLANET)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void createPlanet_WithInvalidData_Returns422UnprocessableEntity() throws Exception {

        mockMvc.perform(post("/planets").content(objMapper.writeValueAsBytes(NULL_PLANET)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/planets").content(objMapper.writeValueAsBytes(INVALID_PLANET)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void createPlanet_WithExistingName_Returns409Conflict() throws Exception {
        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/planets").content(objMapper.writeValueAsBytes(PLANET)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());

    }

    @Test
    public void findById_ByExistingId_ReturnsPlanet() throws Exception {
        when(planetService.findById(1L)).thenReturn(PLANET);
        mockMvc.perform(
                get("/planets/id/{id}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void findById_ByUnexistingId_Returns404NotFound() throws Exception {
        when(planetService.findById(99L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(
                get("/planets/id/{id}", 99L)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void findByName_ByExistingName_ReturnsPlanet() throws Exception {
        String name = "name";

        when(planetService.findByName(name)).thenReturn(PLANET);

        mockMvc.perform(
                get("/planets/name/{name}", name)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void findByName_ByUnexistingName_Throws404NotFound() throws Exception {
        String name = "Unexisting name";

        when(planetService.findByName(name)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(
                get("/planets/name/{name}", name)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void listPlanets_ReturnsAllPlanets() throws Exception {
        when(planetService.findAll(any(), any())).thenReturn(PLANET_LIST);

        mockMvc.perform(
                get("/planets")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(PLANET_LIST.size())));


    }

    @Test
    public void listPlanets_ReturnsFilteredPlanets() throws Exception {
        when(planetService.findAll(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));

        mockMvc.perform(
                        get("/planets")
                                .param("terrain", TATOOINE.getTerrain())
                                .param("climate", TATOOINE.getClimate())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(TATOOINE));

    }

    @Test
    public void listPlanets_ReturnsNoPlanets() throws Exception {
        when(planetService.findAll(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(
                get("/planets")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void removePlanet_WithExistingId_Returns204NoContent() throws Exception {
        mockMvc.perform(
                delete("/planets/{id}", 1L)
        )
                .andExpect(status().isNoContent());

    }

    @Test
    public void removePlanet_WithUnexistingId_Returns404NotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(planetService).deleteById(1L);

        mockMvc.perform(
                delete("/planets{id}", 1L)
        )
                .andExpect(status().isNotFound());
    }
}
