package wrzesniak.rafal.my.multimedia.manager.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.MINUTES;

@EnableCaching
@Configuration
public class CacheConfiguration {

    private static final int DEFAULT_EXPIRY_MINUTES = 30;

    public static final String BOOK_USER_DETAILS_CACHE = "bookUserDetailsCache";
    public static final String GAME_USER_DETAILS_CACHE = "gameUserDetailsCache";
    public static final String MOVIE_USER_DETAILS_CACHE = "movieUserDetailsCache";

    public static final String BOOK_DETAILS_CACHE = "bookDetailsCache";
    public static final String GAME_DETAILS_CACHE = "gameDetailsCache";
    public static final String MOVIE_DETAILS_CACHE = "movieDetailsCache";

    public static final String RECENTLY_DONE_CACHE = "recentlyDoneCache";
    public static final String PRODUCTS_ON_LIST_CACHE = "productsOnListCache";

    @Bean
    public CaffeineCache bookUserDetailsCache() {
        return new CaffeineCache(BOOK_USER_DETAILS_CACHE, caffeineCache());
    }
    @Bean
    public CaffeineCache gameUserDetailsCache() {
        return new CaffeineCache(GAME_USER_DETAILS_CACHE, caffeineCache());
    }
    @Bean
    public CaffeineCache movieUserDetailsCache() {
        return new CaffeineCache(MOVIE_USER_DETAILS_CACHE, caffeineCache());
    }

    @Bean
    public CaffeineCache bookProductCache() {
        return new CaffeineCache(BOOK_DETAILS_CACHE, caffeineCache());
    }
    @Bean
    public CaffeineCache gameProductCache() {
        return new CaffeineCache(GAME_DETAILS_CACHE, caffeineCache());
    }
    @Bean
    public CaffeineCache movieProductCache() {
        return new CaffeineCache(MOVIE_DETAILS_CACHE, caffeineCache());
    }


    @Bean
    public CaffeineCache recentlyDoneCache() {
        return new CaffeineCache(RECENTLY_DONE_CACHE, caffeineCache());
    }

    @Bean
    public CaffeineCache productsOnListCache() {
        return new CaffeineCache(PRODUCTS_ON_LIST_CACHE, caffeineCache());
    }

    private Cache<Object, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(DEFAULT_EXPIRY_MINUTES, MINUTES)
                .build();
    }

}
