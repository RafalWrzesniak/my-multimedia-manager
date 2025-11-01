package wrzesniak.rafal.my.multimedia.manager.domain.game.objects;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GamePlatform {

    PC,
    PS3, PS4, PS5,
    XBOX_ONE, XSX, XBOX_360,
    Switch, Switch2;

    @JsonCreator
    public static GamePlatform fromString(String value) {
        if(value == null) {
            return null;
        }
        if("Xbox Series X/S".equalsIgnoreCase(value)) {
            return XSX;
        }
        if("X360".equalsIgnoreCase(value) || "Xbox 360".equalsIgnoreCase(value)) {
            return XBOX_360;
        }
        if(value.contains("PC")) {
            return PC;
        }
        if(value.contains("Switch 2")) {
            return Switch2;
        }
        if(value.equalsIgnoreCase("Playstation 3")) {
            return PS3;
        }
        if(value.equalsIgnoreCase("Playstation 4")) {
            return PS4;
        }
        if(value.equalsIgnoreCase("Playstation 5")) {
            return PS5;
        }
        return GamePlatform.valueOf(value);
    }
}
