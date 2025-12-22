package com.br.giulianabezerra.starwars_planet_api.commom;

import com.br.giulianabezerra.starwars_planet_api.domain.Planet;

public class PlanetConstants {
        public static final Planet PLANET = new Planet("name", "climate", "terrain");
        public static final Planet INVALID_PLANET = new Planet("", "", "");
        public static final Planet NULL_PLANET = new Planet();
}
