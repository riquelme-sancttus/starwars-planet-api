package com.br.giulianabezerra.starwars_planet_api.domain;

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.INVALID_PLANET;
import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
// @SpringBootTest(classes = PlanetService.class)
public class PlanetServiceTest {

    //@Autowired
    @InjectMocks
    private PlanetService planetService;

    //@MockitoBean
    @Mock
    private PlanetRepository planetRepository;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        //AAA

        // Arrange
        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        // Act
        Planet sut = planetService.create(PLANET); // system under test

        // Assert
        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {

        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> planetService.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void findByIdPlanet_WithExistingId_ReturnsPlanet() {
        Long id = 1L;

        PLANET.setId(1L);

        when(planetRepository.findById(id)).thenReturn(Optional.of(PLANET));

        Planet sut = planetService.findById(id);

        assertThat(sut).isNotNull();
        assertThat(sut.getId()).isEqualTo(PLANET.getId());
        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    public void findByIdPlanet_WithUnexistingId_ThrowsException() {
        Long id = 1L;

        when(planetRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planetService.findById(id))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void findByNamePlanet_WithExistingName_ReturnsPlanet() {
        String name = "name";

        when(planetRepository.findByName(name)).thenReturn(Optional.of(PLANET));

        Planet sut = planetService.findByName(name);

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(name);
        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    public void findByNamePlanet_WithUnexistingName_ThrowsException() {
        String name = "unexisting name";

        when(planetRepository.findByName(name)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planetService.findByName(name))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void findAllPlanets_ReturnsAllPlanets() {
       List<Planet> planets = new ArrayList<>() {{
           add(PLANET);
       }};

        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getName(),
                PLANET.getClimate(), PLANET.getTerrain()));

        when(planetRepository.findAll(query)).thenReturn(planets);

        List<Planet> sut = planetService.findAll(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(1);
        assertThat(sut.getFirst()).isEqualTo(PLANET);
    }

    @Test
    public void findAllPlanets_ReturnsNoPlanets() {
        when(planetRepository.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        List<Planet> sut = planetService.findAll(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isEmpty();;
    }

    @Test
    public void deleteByIdPlanet_WithExistingId_DoesNotThrowAnyException() {
        Long id = 1L;

        when(planetRepository.existsById(id)).thenReturn(true);
        doNothing().when(planetRepository).deleteById(id);

        assertThatCode(() -> planetService.deleteById(id))
                .doesNotThrowAnyException();

        verify(planetRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void deleteByIdPlanet_WithUnexistingId_TrowsException() {
        Long id = 99L;

        when(planetRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> planetService.deleteById(id))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                    .isEqualTo(HttpStatus.NOT_FOUND);

        verify(planetRepository, never()).deleteById(anyLong());
    }
}
