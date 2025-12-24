package com.br.giulianabezerra.starwars_planet_api.web;

import com.br.giulianabezerra.starwars_planet_api.domain.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.PLANET;
import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.INVALID_PLANET;
import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.NULL_PLANET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
