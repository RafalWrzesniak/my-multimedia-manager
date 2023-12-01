package wrzesniak.rafal.my.multimedia.manager.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.CorsFilter;
import wrzesniak.rafal.my.multimedia.manager.domain.user.DynamoUserDetailService;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String secret;
    private final ObjectMapper objectMapper;
    private final RestAuthenticationSuccessHandler successHandler;
    private final RestAuthenticationFailureHandler failureHandler;
    private final DynamoUserDetailService dynamoUserDetailService;


    public SecurityConfig(ObjectMapper objectMapper,
                          RestAuthenticationSuccessHandler successHandler,
                          RestAuthenticationFailureHandler failureHandler,
                          @Value("${application.jwt.secret}") String secret,
                          DynamoUserDetailService dynamoUserDetailService) {
        this.objectMapper = objectMapper;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.secret = secret;
        this.dynamoUserDetailService = dynamoUserDetailService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(dynamoUserDetailService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http
            .csrf()
            .ignoringAntMatchers("/login")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .authorizeRequests()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/register/**").permitAll()
                .antMatchers("/simple/**").permitAll()
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
            .and()
            .sessionManagement().sessionCreationPolicy(STATELESS)
            .and()
            .addFilter(authenticationFilter())
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), dynamoUserDetailService, secret))
            .exceptionHandling()
            .authenticationEntryPoint(new HttpStatusEntryPoint(UNAUTHORIZED))
            .and()
            .headers().frameOptions().disable();
    }

    public JsonObjectAuthenticationFilter authenticationFilter() throws Exception {
        JsonObjectAuthenticationFilter authenticationFilter = new JsonObjectAuthenticationFilter(objectMapper);
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setAuthenticationFailureHandler(failureHandler);
        authenticationFilter.setAuthenticationManager(super.authenticationManager());
        return authenticationFilter;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedOrigins(List.of("https://master.d5i6lke35o8tb.amplifyapp.com", "http://localhost:3000"));
////        configuration.setAllowedOrigins(List.of("https://master.d5i6lke35o8tb.amplifyapp.com"));
////        configuration.setAllowedOrigins(List.of("*"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTION"));
//        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With","Origin","Content-Type","Accept","Authorization","x-xsrf-token"));
//        configuration.setExposedHeaders(List.of("Authorization", "x-xsrf-token", "Access-Control-Allow-Headers", "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://master.d5i6lke35o8tb.amplifyapp.com", "http://localhost:3000"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        return filter;
    }
}
