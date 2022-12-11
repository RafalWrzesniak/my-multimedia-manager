package wrzesniak.rafal.my.multimedia.manager.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebOperations {

    private final ImdbService imdbService;

    public void downloadResizedImageTo(URL originalImageUrl, Path savePath) {
        URL resizedImageUrl = imdbService.findResizedImageUrl(originalImageUrl);
        downloadImageToDirectory(resizedImageUrl, savePath);
    }

    private void downloadImageToDirectory(URL imageUrl, Path pathToSaveImage) {
        try (InputStream inputStream = imageUrl.openStream()) {
            Files.copy(inputStream, pathToSaveImage, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn("Failed to download image from \"{}\"", imageUrl);
        }
        log.debug("Image downloaded from \"{}\"", imageUrl);
    }

    public Document parseUrl(URL websiteUrl) throws IOException {
        return Jsoup.parse(websiteUrl, 3000);
    }
}
