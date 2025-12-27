package com.br.giulianabezerra.starwars_planet_api.commom;

import com.br.giulianabezerra.starwars_planet_api.domain.Planet;

import java.util.ArrayList;
import java.util.List;

public class PlanetConstants {
        public static final Planet PLANET = new Planet("name", "climate", "terrain");
        public static final Planet INVALID_PLANET = new Planet("", "", "");
        public static final Planet NULL_PLANET = new Planet();

        public static final Planet TATOOINE = new Planet(1L, "Tatooine", "arid", "desert");
        public static final Planet ALDERAAN = new Planet(2L, "Alderaan", "temperate", "grasslands, mountains");
        public static final Planet YAVIN_IV = new Planet(3L, "Yavin IV", "temperate, tropical", "jungle, rainforest");
        public static final List<Planet> PLANET_LIST = new ArrayList<>() {
            {
                add(TATOOINE);
                add(ALDERAAN);
                add(YAVIN_IV);
            }
        };
}
