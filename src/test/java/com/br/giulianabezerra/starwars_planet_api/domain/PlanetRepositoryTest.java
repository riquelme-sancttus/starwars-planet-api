package com.br.giulianabezerra.starwars_planet_api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = repository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

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

    @Test
    public void createPlanet_WithExistingName_ThrowsException(){
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        assertThatThrownBy(() -> repository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void findById_ByExistingId_ReturnsPlanet(){
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> optionalPlanet = repository.findById(1L);

        assertThat(optionalPlanet).isPresent();
        assertThat(optionalPlanet.get()).isEqualTo(PLANET);
    }

    @Test
    public void findById_ByUnexistingId_ReturnsEmpty() {
        Optional<Planet> optionalPlanet = repository.findById(1L);
        assertThat(optionalPlanet).isEmpty();
    }

    @Test
    public void findByName_ByExistingName_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> optionalPlanet = repository.findByName(planet.getName());

        assertThat(optionalPlanet).isPresent();
        assertThat(optionalPlanet.get()).isEqualTo(PLANET);
    }

    @Test
    public void findByName_ByUnexistingName_ReturnsEmpty() {
        Optional<Planet> optionalPlanet = repository.findByName("name");

        assertThat(optionalPlanet).isEmpty();
    }

}