package com.draganovik.apigateway.configurations;

import com.draganovik.apigateway.authmethods.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfiguration {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.csrf().disable().authorizeExchange()
                .pathMatchers("/currency-exchange/**").hasAnyRole()

                .pathMatchers("/crypto-exchange/**").hasAnyRole()

                .pathMatchers("/currency-conversion/**").hasRole("USER")

                .pathMatchers("/crypto-conversion/**").hasRole("USER")

                .pathMatchers("/trade-service/**").hasRole("USER")

                .pathMatchers("/transfer-service/**").hasRole("USER")

                .pathMatchers("/user-service/register").permitAll()
                .pathMatchers("/user-service/validate").permitAll() // Do not change, will cause an inf loop
                .pathMatchers(HttpMethod.GET, "/user-service/users/**").hasAnyRole("ADMIN", "OWNER")
                .pathMatchers(HttpMethod.POST, "/user-service/users/create").hasAnyRole("ADMIN", "OWNER")
                .pathMatchers(HttpMethod.PUT, "/user-service/users/update/{email}").hasAnyRole("ADMIN", "OWNER")
                .pathMatchers(HttpMethod.DELETE, "/user-service/users/delete/{email}").hasAnyRole("OWNER")

                .pathMatchers(HttpMethod.GET, "/bank-account").hasRole("USER")
                .pathMatchers(HttpMethod.GET, "/bank-account/{email}").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/bank-account/{email}").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/bank-account/{email}").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/bank-account/{email}").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/bank-account/{email}/update").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/bank-account/{email}/balance").hasRole("ADMIN")

                .pathMatchers(HttpMethod.GET, "/crypto-wallet").hasRole("USER")
                .pathMatchers(HttpMethod.GET, "/crypto-wallet/{email}").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/crypto-wallet/{email}").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/crypto-wallet/{email}").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/crypto-wallet/{email}").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/crypto-wallet/{email}/update").hasRole("ADMIN")

                .and()
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .addFilterBefore(new JwtAuthenticationFilter(webClientBuilder), SecurityWebFiltersOrder.AUTHORIZATION)
                .authorizeExchange();
        return http.build();
    }
}
