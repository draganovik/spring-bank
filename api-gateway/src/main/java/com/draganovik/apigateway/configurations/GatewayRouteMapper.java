package com.draganovik.apigateway;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

public class GatewayRouteMapper {

    public static void mapRoutesUserService(RouteLocatorBuilder.Builder rlb) {
        rlb.route(p -> p.path("/user-service/**").uri("lb://user-service"));
    }

    public static void mapRoutesBankAccount(RouteLocatorBuilder.Builder rlb) {
        rlb.route(p -> p.path("/bank-account/**").uri("lb://bank-account"));
    }

    public static void mapRoutesCurrencyExchange(RouteLocatorBuilder.Builder rlb) {
        rlb.route(p -> p.path("/currency-exchange/**").uri("lb://currency-exchange"));
    }

    public static void mapRoutesCurrencyConversion(RouteLocatorBuilder.Builder rlb) {
        rlb.route(p -> p.path("/currency-conversion-feign").uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion")
                        .filters(f -> f.rewritePath("currency-conversion", "currency-conversion-feign"))
                        .uri("lb://currency-conversion"));
    }
}
