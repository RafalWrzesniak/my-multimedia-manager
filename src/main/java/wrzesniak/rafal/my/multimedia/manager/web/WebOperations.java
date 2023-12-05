package wrzesniak.rafal.my.multimedia.manager.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebOperations {

    public Document parseUrl(URL websiteUrl) throws IOException {
        return Jsoup.parse(websiteUrl, 3000);
    }
}
