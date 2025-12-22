package com.br.giulianabezerra.starwars_planet_api.domain;

import com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = repository.save(PLANET);

        Planet sut = entityManager.find(Planet.class, planet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(planet.getName());
        assertThat(sut.getClimate()).isEqualTo(planet.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(planet.getTerrain());
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        assertThatThrownBy(() -> repository.save(NULL_PLANET));
        assertThatThrownBy(() -> repository.save(INVALID_PLANET));

    }
}