package wrzesniak.rafal.my.multimedia.manager.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

//    @Bean
//    public Docket swaggerApi() {
//        return new Docket(DocumentationType. SWAGGER_2 )
//                .ignoredParameterTypes(UsernamePasswordAuthenticationToken.class)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("wrzesniak.rafal.my.multimedia.manager"))
//                .build()
//                .securitySchemes(singletonList(createSchema()))
//                .securityContexts(singletonList(createContext()));
//    }
//
//    private SecurityContext createContext() {
//        return SecurityContext.builder()
//                .securityReferences(createRef())
//                .forPaths(PathSelectors.any())
//                .build();
//    }
//
//    private List<SecurityReference> createRef() {
//        AuthorizationScope authorizationScope = new AuthorizationScope(
//                "global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return singletonList(new SecurityReference("apiKey", authorizationScopes));
//    }
//
//    private SecurityScheme createSchema() {
//        return new ApiKey("apiKey", "Authorization", "header");
//    }

}
