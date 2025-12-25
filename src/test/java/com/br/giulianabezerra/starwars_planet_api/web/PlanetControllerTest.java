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

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.PLANET;
import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.INVALID_PLANET;
import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.NULL_PLANET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
}
