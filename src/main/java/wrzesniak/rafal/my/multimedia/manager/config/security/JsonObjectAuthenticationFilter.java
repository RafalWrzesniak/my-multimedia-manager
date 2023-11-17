//package wrzesniak.rafal.my.multimedia.manager.config.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//public class JsonObjectAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
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
//            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                    authRequest.getUsername(), authRequest.getPassword()
//            );
//            setDetails(request, token);
//            return this.getAuthenticationManager().authenticate(token);
//        } catch (IOException e) {
//            throw new IllegalArgumentException(e.getMessage());
//        }
//    }
//}