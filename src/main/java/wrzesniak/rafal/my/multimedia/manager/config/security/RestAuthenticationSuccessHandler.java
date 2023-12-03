package wrzesniak.rafal.my.multimedia.manager.config.security;

//@Slf4j
//@Component
public class RestAuthenticationSuccessHandler {// extends SimpleUrlAuthenticationSuccessHandler {

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
//        setUseReferer(true);
//        UserDetails principal = (UserDetails) authentication.getPrincipal();
//        String token = JWT.create()
//                .withSubject(principal.getUsername())
//                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
//                .sign(Algorithm.HMAC256(secret));
//        log.info("Success login for user `{}`", principal.getUsername());
//        response.addHeader("Authorization", "Bearer " + token);
//    }

}