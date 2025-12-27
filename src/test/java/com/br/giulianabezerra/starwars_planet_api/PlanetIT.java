package com.br.giulianabezerra.starwars_planet_api;

import com.br.giulianabezerra.starwars_planet_api.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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
    public void createPlanet_WithExistingName_Returns409Conflict(){
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
}
