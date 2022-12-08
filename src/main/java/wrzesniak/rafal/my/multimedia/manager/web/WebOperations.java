package wrzesniak.rafal.my.multimedia.manager.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class WebOperations {

    public boolean downloadImageToDirectory(URL imageUrl, Path pathToSaveImage) {
        try (InputStream inputStream = imageUrl.openStream()) {
            Files.copy(inputStream, pathToSaveImage, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn("Failed to download image from \"{}\"", imageUrl);
            return false;
        }
        log.debug("Image downloaded from \"{}\"", imageUrl);
        return true;
    }
}
