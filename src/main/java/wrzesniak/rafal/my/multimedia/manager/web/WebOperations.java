package wrzesniak.rafal.my.multimedia.manager.web;

import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.service.S3Service;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbService;

import java.io.File;
import java.io.FileOutputStream;
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
    private final S3Service s3Service;

    public void downloadResizedImageTo(URL originalImageUrl, Path savePath) {
        URL resizedImageUrl = imdbService.findResizedImageUrl(originalImageUrl);
        downloadImageToDirectory(resizedImageUrl, savePath);
    }

    public void downloadResizedImageToS3(URL originalImageUrl, Path savePath) {
        URL resizedImageUrl = imdbService.findResizedImageUrl(originalImageUrl);
        File tempFile;
        try {
            tempFile = File.createTempFile("prefix", "suffix");
            tempFile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(tempFile);
            InputStream inputStream = resizedImageUrl.openStream();
            IOUtils.copy(inputStream, out);
        } catch (IOException ioException) {
            log.warn("IOException while getting image from `{}`", originalImageUrl);
            return;
        }
        s3Service.putObject(savePath, tempFile);
    }

    public void downloadImageToDirectory(URL imageUrl, Path pathToSaveImage) {
        try (InputStream inputStream = imageUrl.openStream()) {
            Files.copy(inputStream, pathToSaveImage, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn("Failed to download image from \"{}\", because of {}", imageUrl, e.getMessage());
        }
        log.debug("Image downloaded from \"{}\"", imageUrl);
    }

    public Document parseUrl(URL websiteUrl) throws IOException {
        return Jsoup.parse(websiteUrl, 3000);
    }
}
