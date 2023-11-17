//package wrzesniak.rafal.my.multimedia.manager.config.security;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Date;
//
//@Slf4j
//@Component
//public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final String secret;
//    private final long expirationTime;
//
//    public RestAuthenticationSuccessHandler(
//            @Value("${application.jwt.secret}") String secret,
//            @Value("${application.jwt.expirationTime}") long expirationTime) {
//        this.secret = secret;
//        this.expirationTime = expirationTime;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        UserDetails principal = (UserDetails) authentication.getPrincipal();
//        String token = JWT.create()
//                .withSubject(principal.getUsername())
//                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
//                .sign(Algorithm.HMAC256(secret));
//        log.info("Success login for user `{}`", principal.getUsername());
//        response.addHeader("Authorization", "Bearer " + token);
//    }
//
//}