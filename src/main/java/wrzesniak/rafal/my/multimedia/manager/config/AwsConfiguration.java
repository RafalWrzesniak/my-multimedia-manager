package wrzesniak.rafal.my.multimedia.manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration("awsProperties")
@ConfigurationProperties(prefix = "aws")
public class AwsConfiguration {

    private String accessKey;
    private String secretKey;
    private String s3BucketName;

}
