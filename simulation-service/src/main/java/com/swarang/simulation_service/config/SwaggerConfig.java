package com.swarang.simulation_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Simulation Service", description = "API for simulation service", version = "1.0.0"))
public class SwaggerConfig {

}
