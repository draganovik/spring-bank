package com.draganovik.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAuth {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.csrf().disable().authorizeExchange()
                .pathMatchers("/currency-exchange/**").permitAll()
                .pathMatchers("/crypto-exchange/**").permitAll()

                .pathMatchers("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}").hasRole("USER")
                .pathMatchers("/crypto-conversion/**").hasRole("USER")

                .pathMatchers("/user-service/register").permitAll()
                .pathMatchers("/user-service/validate").permitAll()
                .pathMatchers("/user-service/users").hasAnyRole("ADMIN", "OWNER")

                .and()
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .addFilterBefore(new JwtAuthenticationFilter(webClientBuilder), SecurityWebFiltersOrder.AUTHORIZATION)
                .authorizeExchange();
        return http.build();
    }
}
