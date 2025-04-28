package com.eni.winecellar.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Wine Cellar API",
                description = "API for wine bottle management",
                version = "1.0"
        )
)
public class OpenApiConfiguration {
}
