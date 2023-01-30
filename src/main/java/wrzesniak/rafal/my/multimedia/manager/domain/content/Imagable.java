package wrzesniak.rafal.my.multimedia.manager.domain.content;

import java.nio.file.Path;

public interface Imagable {

    String getUniqueId();

    default Path getImagePath() {
        return Path.of("G:\\my-multimedia-manager","images", getClass().getSimpleName().toLowerCase(), getUniqueId().concat(".jpg"));
    }

}
