package com.br.giulianabezerra.starwars_planet_api;

import com.br.giulianabezerra.starwars_planet_api.domain.Planet;
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
@Sql(scripts = "/import_planets.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class PlanetIT {

    @LocalServerPort
    private int port;

    private RestClient restClient = RestClient.create();

    private String getBaseUrl() {
        return "http://localhost:" + port + "/planets";
    }

    @Test
    public void createPlanet_ValidData_Returns201Created() {
        ResponseEntity<Planet> sut = restClient
                .post()
                .uri(getBaseUrl())
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
                        .uri(getBaseUrl())
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
                        .uri(getBaseUrl())
                        .body(alderaan)
                        .retrieve()
                        .toEntity(Planet.class)
        ).isInstanceOf(HttpClientErrorException.Conflict.class);
    }

    @Test
    public void findById_ByExistingId_ReturnsPlanet() {
        String id = getBaseUrl() + "/id" + "/3";

        ResponseEntity<Planet> sut = restClient.get().uri(id).retrieve().toEntity(Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(YAVIN_IV);
    }

    @Test
    public void findById_ByUnexistingId_Returns404NotFound() {
        String id = getBaseUrl() + "/id" + "/99";

        assertThatThrownBy(() ->
                restClient.get().uri(id).retrieve().toEntity(Planet.class)
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    public void findByName_ByExistingName_ReturnsPlanet() {
        String name = getBaseUrl() + "/name" + "/Yavin IV";

        ResponseEntity<Planet> sut = restClient.get().uri(name).retrieve().toEntity(Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(YAVIN_IV);
    }

    @Test
    public void findByName_ByUnexistingName_Returns404NotFound() {
        String name = getBaseUrl() + "/name/" + "Unexisting Name xD";

        assertThatThrownBy(() ->
                restClient.get().uri(name).retrieve().toEntity(Planet.class)
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    public void listPlanets_ReturnsAll() {
        ResponseEntity<List<Planet>> sut =
                restClient
                        .get()
                        .uri(getBaseUrl())
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
                .uri(getBaseUrl() + "?climate=tropical")
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
                .uri(getBaseUrl() + "?terrain=jungle")
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
                .uri(getBaseUrl() + "?climate=temperate&terrain=rainforest")
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
                .uri(getBaseUrl() + "?climate=frozen&terrain=ice")
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
                .uri(getBaseUrl() + "/2")
                .retrieve()
                .toBodilessEntity();

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void removePlanet_WithUnexistingId_Returns404NotFound() {
        assertThatThrownBy(() ->
                restClient
                        .delete()
                        .uri(getBaseUrl() + "/99")
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.NotFound.class);
    }
}
