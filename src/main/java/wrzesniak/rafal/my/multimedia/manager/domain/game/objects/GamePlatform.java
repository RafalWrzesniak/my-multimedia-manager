package wrzesniak.rafal.my.multimedia.manager.domain.game.objects;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GamePlatform {

    PC,
    PS3, PS4, PS5,
    XBOX_ONE, XSX, XBOX_360,
    Switch;

    @JsonCreator
    public static GamePlatform fromString(String value) {
        if ("X360".equalsIgnoreCase(value)) {
            return XBOX_360;
        }
        return GamePlatform.valueOf(value);
    }
}
