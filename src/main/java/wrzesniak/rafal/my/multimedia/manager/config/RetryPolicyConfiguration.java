package wrzesniak.rafal.my.multimedia.manager.config;

import dev.failsafe.RetryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class RetryPolicyConfiguration {

    @Bean
    public RetryPolicy<Object> createIoExceptionRetryPolicy() {
        return RetryPolicy.builder()
                .handle(IOException.class)
                .withDelay(Duration.ofSeconds(1))
                .withMaxRetries(3)
                .build();
    }




}
