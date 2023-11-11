package com.draganovik.apigateway.authmethods.jwt;

import com.draganovik.apigateway.models.UserValidationResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class JwtAuthenticationFilter implements WebFilter {

    private final WebClient webClient;

    public JwtAuthenticationFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);


        if (token == null || token.isEmpty()) {
            return chain.filter(exchange);
        }

        return webClient.get()
                .uri("lb://user-service/user-service/validate")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(UserValidationResponse.class) // Assume UserValidationResponse has email and role
                .flatMap(validationResponse -> {
                    // Populate the SecurityContext
                    System.out.println("Validation Response: " + validationResponse.getEmail() + " " + validationResponse.getRole());
                    Authentication authentication = new JwtAuthenticationToken(
                            validationResponse.getEmail(),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + validationResponse.getRole().name()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // Forward the email and role to downstream services
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(r -> r.header("X-User-Email", validationResponse.getEmail())
                                    .header("X-User-Role", validationResponse.getRole().name()))
                            .build();
                    return mutatedExchange.getSession()
                            .flatMap(webSession -> {
                                webSession.getAttributes().put("SPRING_SECURITY_CONTEXT", new SecurityContextImpl(authentication));
                                return chain.filter(mutatedExchange);
                            })
                            .onErrorResume(e -> chain.filter(exchange));
                })
                .onErrorResume(e -> chain.filter(exchange));
    }
}
