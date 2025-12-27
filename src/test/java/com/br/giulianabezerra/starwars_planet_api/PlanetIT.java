package com.br.giulianabezerra.starwars_planet_api;

import com.br.giulianabezerra.starwars_planet_api.domain.Planet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Sql(scripts = "/import_planets.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PlanetIT {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/planets")
                .build();
    }

    @Test
    public void createPlanet_ValidData_Returns201Created() {
        ResponseEntity<Planet> sut = restClient
                .post()
                .uri("")
                .body(PLANET)
                .retrieve()
                .toEntity(Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getBody().getTerrain()).isEqualTo(PLANET.getTerrain());
        assertThat(sut.getBody().getClimate()).isEqualTo(PLANET.getClimate());
    }

    @Test
    public void createPlanet_WithInvalidData_Returns422UnprocessableEntity() {
        assertThatThrownBy(() ->
                restClient
                        .post()
                        .uri("")
                        .body(INVALID_PLANET)
                        .retrieve()
                        .toEntity(Planet.class)
        ).isInstanceOf(HttpClientErrorException.UnprocessableContent.class);
    }

    @Test
    public void createPlanet_WithExistingName_Returns409Conflict() {
        Planet alderaan = new Planet(null, ALDERAAN.getName(), ALDERAAN.getClimate(), ALDERAAN.getTerrain());

        assertThatThrownBy(() ->
                restClient
                        .post()
                        .uri("")
                        .body(alderaan)
                        .retrieve()
                        .toEntity(Planet.class)
        ).isInstanceOf(HttpClientErrorException.Conflict.class);
    }

    @Test
    public void findById_ByExistingId_ReturnsPlanet() {
        ResponseEntity<Planet> sut =
                restClient
                        .get()
                        .uri("/id/{id}", 3)
                        .retrieve()
                        .toEntity(Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(YAVIN_IV);
    }

    @Test
    public void findById_ByUnexistingId_Returns404NotFound() {
        assertThatThrownBy(() ->
                restClient
                        .get()
                        .uri("/id/{id}", 99)
                        .retrieve()
                        .toEntity(Planet.class)
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    public void findByName_ByExistingName_ReturnsPlanet() {
        ResponseEntity<Planet> sut = restClient
                .get()
                .uri("/name/{name}", "Yavin IV")
                .retrieve()
                .toEntity(Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(YAVIN_IV);
    }

    @Test
    public void findByName_ByUnexistingName_Returns404NotFound() {
        assertThatThrownBy(() ->
                restClient
                        .get()
                        .uri("/name/{name}", "Unexisting Name xD")
                        .retrieve()
                        .toEntity(Planet.class)
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    public void listPlanets_ReturnsAll() {
        ResponseEntity<List<Planet>> sut =
                restClient
                        .get()
                        .uri("")
                        .retrieve()
                        .toEntity(new ParameterizedTypeReference<List<Planet>>() {});

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isNotEmpty();
        assertThat(sut.getBody()).hasSize(3);
    }

    @Test
    public void listPlanets_ByClimate_ReturnsFilteredPlanets() {
        ResponseEntity<List<Planet>> sut = restClient
                .get()
                .uri("?climate={climate}", "tropical")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Planet>>() {
                });

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isNotEmpty();
        assertThat(sut.getBody()).hasSize(1);
        assertThat(sut.getBody().getFirst()).isEqualTo(YAVIN_IV);
    }

    @Test
    public void listPlanets_ByTerrain_ReturnsFilteredPlanets() {
        ResponseEntity<List<Planet>> sut = restClient
                .get()
                .uri("?terrain={terrain}", "jungle")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Planet>>() {});

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isNotEmpty();
        assertThat(sut.getBody()).hasSize(1);
        assertThat(sut.getBody().getFirst()).isEqualTo(YAVIN_IV);
    }

    @Test
    public void listPlanets_ByClimateAndTerrain_ReturnsFilteredPlanets() {
        ResponseEntity<List<Planet>> sut = restClient
                .get()
                .uri("?climate={climate}&terrain={terrain}", "temperate", "rainforest")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Planet>>() {});

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isNotEmpty();
        assertThat(sut.getBody().getFirst()).isEqualTo(YAVIN_IV);
    }

    @Test
    public void listPlanets_WithNonExistingFilters_ReturnsEmpty() {
        ResponseEntity<List<Planet>> sut = restClient
                .get()
                .uri("?climate={climate}&terrain={terrain}", "999X", "999Y")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Planet>>() {
                });

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEmpty();
    }

    @Test
    public void removePlanet_WithExistingId_Returns204NoContent() {
        ResponseEntity<Void> sut = restClient
                .delete()
                .uri("/{id}", 2)
                .retrieve()
                .toBodilessEntity();

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void removePlanet_WithUnexistingId_Returns404NotFound() {
        assertThatThrownBy(() ->
                restClient
                        .delete()
                        .uri("/{id}", 99)
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }
}
