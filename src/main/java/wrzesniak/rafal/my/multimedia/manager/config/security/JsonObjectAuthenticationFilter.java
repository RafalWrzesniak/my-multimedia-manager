package wrzesniak.rafal.my.multimedia.manager.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JsonObjectAuthenticationFilter {//extends UsernamePasswordAuthenticationFilter {

//    private final ObjectMapper objectMapper;
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        try {
//            BufferedReader reader = request.getReader();
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//            LoginCredentials authRequest = objectMapper.readValue(sb.toString(), LoginCredentials.class);
//            log.info("Attempting to authenticate for user {}", authRequest.getUsername());
//            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                    authRequest.getUsername(), authRequest.getPassword()
//            );
//            setDetails(request, token);
//            return this.getAuthenticationManager().authenticate(token);
//        } catch (IOException e) {
//            throw new IllegalArgumentException(e.getMessage());
//        }
//    }
}