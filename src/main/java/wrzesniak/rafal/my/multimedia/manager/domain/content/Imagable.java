package wrzesniak.rafal.my.multimedia.manager.domain.content;

import java.nio.file.Path;
import java.util.List;

public interface Imagable {

    String getUniqueId();

    default List<Path> getImagePath() {
        return List.of(
                Path.of("E:\\react-repos","my-multimedia-manager-front", "public" ,"images", getClass().getSimpleName().toLowerCase(), getUniqueId().concat(".jpg")),
                Path.of("G:\\my-multimedia-manager","images", getClass().getSimpleName().toLowerCase(), getUniqueId().concat(".jpg"))
        );
    }

}
