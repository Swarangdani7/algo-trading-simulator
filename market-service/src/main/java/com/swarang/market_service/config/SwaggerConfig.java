package com.swarang.market_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Market Service", description = "API for market service", version = "1.0.0"))
public class SwaggerConfig {

}
