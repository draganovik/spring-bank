package com.draganovik.apigateway;

import java.util.function.Function;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		
		return builder
				.routes()
				.route(p -> p.path("/@Depaiva1997").uri("http://youtube.com"))
				.route(p -> p.path("/currency-exchange/**").uri("lb://currency-exchange"))
				.build();
	}
	
	//http://localhost:8765/currency-exchange/from/USD/to/RSD
}