package com.br.giulianabezerra.starwars_planet_api.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = repository.save(new Planet(PLANET.getName(), PLANET.getClimate(), PLANET.getTerrain()));

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

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void createPlanet_WithExistingName_ThrowsException(){
        Planet planet = new Planet(ALDERAAN.getName(), ALDERAAN.getClimate(), ALDERAAN.getTerrain());

        assertThatThrownBy(() -> repository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    @Sql("/import_planets.sql")
    @Test
    public void findById_ByExistingId_ReturnsPlanet(){
        Optional<Planet> optionalPlanet = repository.findById(1L);

        assertThat(optionalPlanet).isPresent();
        assertThat(optionalPlanet.get()).isEqualTo(TATOOINE);
    }

    @Test
    public void findById_ByUnexistingId_ReturnsEmpty() {
        Optional<Planet> optionalPlanet = repository.findById(1L);
        assertThat(optionalPlanet).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void findByName_ByExistingName_ReturnsPlanet() {
        Optional<Planet> optionalPlanet = repository.findByName(ALDERAAN.getName());

        assertThat(optionalPlanet).isPresent();
        assertThat(optionalPlanet.get()).isEqualTo(ALDERAAN);
    }

    @Test
    public void findByName_ByUnexistingName_ReturnsEmpty() {
        Optional<Planet> optionalPlanet = repository.findByName("name");

        assertThat(optionalPlanet).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void listPlanets_ReturnsAllPlanets(){
        Example<Planet> queryWithoutFilter = QueryBuilder.makeQuery(new Planet());

        List<Planet> sut = repository.findAll(queryWithoutFilter);

        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(3);
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void listPlanets_ReturnsFilteredPlanets(){
        Example<Planet> queryWithFilter = QueryBuilder.makeQuery(
                new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain())
        );

        List<Planet> sut = repository.findAll(queryWithFilter);

        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(1);
        assertThat(sut.getFirst()).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ReturnsNoPlanet(){
        List<Planet> sut = repository.findAll(QueryBuilder.makeQuery(INVALID_PLANET));
        assertThat(sut).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void removePlanet_WithExistingId_RemovesPlanetFromDatabase(){
        repository.deleteById(2L);

        Planet removedPlanet = testEntityManager.find(Planet.class, 2L);
        assertThat(removedPlanet).isNull();
    }

    @Test
    public void removePlanet_WithUnexistingId_DoesNotThrowException(){
        assertThatCode(() -> repository.deleteById(1L))
                .doesNotThrowAnyException();
    }

}