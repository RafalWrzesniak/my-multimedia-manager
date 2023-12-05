package wrzesniak.rafal.my.multimedia.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Component
@RequiredArgsConstructor
public class AwsSecretsManager {

    private final SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build();


    public String getSecret(String secretName) {
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        GetSecretValueResponse getSecretValueResponse;
        getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);
        return getSecretValueResponse.secretString();
    }

}
