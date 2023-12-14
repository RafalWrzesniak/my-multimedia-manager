package wrzesniak.rafal.my.multimedia.manager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.service.AwsSecretsManager;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenDecoder {

    private final ObjectMapper objectMapper;
    private final AwsSecretsManager awsSecretsManager;
    public static final String TOKEN_HEADER = "Authorization";
    private static final String ADMIN_DATA_SECRET = "prod/admin/data";

    public String parseUsernameFromAuthorizationHeader(String jwtToken) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = jwtToken.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        try {
            String username = objectMapper.readValue(payload, JwtPayload.class).getUsername();
            AdminSecret adminSecret = objectMapper.readValue(awsSecretsManager.getSecret(ADMIN_DATA_SECRET), AdminSecret.class);
            if(username.equals(adminSecret.adminCognitoUsername())) {
                username = adminSecret.adminOriginalUsername();
            }
            return username;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse authorization header: {}", jwtToken);
            throw new IllegalArgumentException("Failed to parse authorization header");
        }
    }

    private record JwtPayload(String sub) {
        public String getUsername() {
            return sub;
        }
    }

    private record AdminSecret(String adminCognitoUsername,
                               String adminOriginalUsername) {}
}
