package com.br.giulianabezerra.starwars_planet_api.web;

import com.br.giulianabezerra.starwars_planet_api.domain.Planet;
import com.br.giulianabezerra.starwars_planet_api.domain.PlanetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/planets")
public class PlanetController {
    private final PlanetService service;

    public PlanetController(PlanetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody Planet planet) {
        var planetCreated = service.create(planet);
        return ResponseEntity.status(HttpStatus.CREATED).body(planetCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> findById(@PathVariable Long id) {
        var planetFound = service.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(planetFound);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Planet> findByName(@PathVariable String name){
        var planetFound = service.findByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(planetFound);
    }
}
