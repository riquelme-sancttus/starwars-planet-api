package com.br.giulianabezerra.starwars_planet_api.web;

import com.br.giulianabezerra.starwars_planet_api.domain.Planet;
import com.br.giulianabezerra.starwars_planet_api.domain.PlanetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planets")
public class PlanetController {
    private final PlanetService service;

    public PlanetController(PlanetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody @Valid Planet planet) {
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

    @GetMapping
    public ResponseEntity<List<Planet>> findAll(@RequestParam (required = false) String terrain,
                                                @RequestParam (required = false) String climate) {
        var planetList = service.findAll(terrain, climate);
        return ResponseEntity.ok(planetList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
