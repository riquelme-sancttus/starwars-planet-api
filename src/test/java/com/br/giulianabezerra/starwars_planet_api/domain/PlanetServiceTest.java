package com.br.giulianabezerra.starwars_planet_api.domain;

import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.INVALID_PLANET;
import static com.br.giulianabezerra.starwars_planet_api.commom.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
}
