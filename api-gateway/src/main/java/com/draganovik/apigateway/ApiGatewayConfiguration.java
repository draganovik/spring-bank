package com.draganovik.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@ImportAutoConfiguration({RouteLocatorBuilder.class, PathRoutePredicateFactory.class, RewritePathGatewayFilterFactory.class})
@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder rlb = builder.routes();
        ServiceRoutes.mapRoutesBankAccount(rlb);
        ServiceRoutes.mapRoutesCurrencyConversion(rlb);
        ServiceRoutes.mapRoutesCurrencyExchange(rlb);
        return rlb.build();
    }
}

