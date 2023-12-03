package wrzesniak.rafal.my.multimedia.manager.config.security;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthorizationFilter {//extends BasicAuthenticationFilter {

//    private static final String TOKEN_HEADER = "Authorization";
//    private static final String TOKEN_PREFIX = "Bearer ";
//    private final UserDetailsService userDetailsService;
//    private final String secret;
//
//    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
//                                  UserDetailsService userDetailsService,
//                                  String secret) {
//        super(authenticationManager);
//        this.userDetailsService = userDetailsService;
//        this.secret = secret;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        log.info("Filtering JWT authorization");
//        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
//        if (authentication == null) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        filterChain.doFilter(request, response);
//    }
//
//    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
//        String token = request.getHeader(TOKEN_HEADER);
//        if (token != null && token.startsWith(TOKEN_PREFIX)) {
//            String userName = JWT.require(Algorithm.HMAC256(secret))
//                    .build()
//                    .verify(token.replace(TOKEN_PREFIX, ""))
//                    .getSubject();
//            if (userName != null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
//                return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
//            }
//        }
//        return null;
//    }
}