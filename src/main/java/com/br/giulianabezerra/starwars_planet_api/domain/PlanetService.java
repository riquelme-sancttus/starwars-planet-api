package com.br.giulianabezerra.starwars_planet_api.domain;

import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.br.giulianabezerra.starwars_planet_api.domain.QueryBuilder.makeQuery;

@Service
public class PlanetService {
    private final PlanetRepository repository;

    public PlanetService(PlanetRepository repository) {
        this.repository = repository;
    }

    public Planet create (Planet planet) {
        return repository.save(planet);
    }

    public Planet findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Planet findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Planet> findAll(String terrain, String climate) {
        Example<Planet> query = makeQuery(new Planet(climate, terrain));
        return repository.findAll(query);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
    }
}
