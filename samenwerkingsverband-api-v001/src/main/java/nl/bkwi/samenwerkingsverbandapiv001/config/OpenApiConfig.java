package nl.bkwi.samenwerkingsverbandapiv001.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class contains configuration for the generation of Swagger API documentation.
 */
@Configuration
@Slf4j
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {

        Info info = new Info()
                .title("Samenwerkingsverband API")
                .version("001")
                .description("Overzicht van de gemeentes in een samenwerkingsverband, met opgave van de voorkeursgemeente, voor een gegeven gebruiker, geïdentificeerd met een DN");

        return new OpenAPI()
                .components(new Components())
                .info(info)
                ;
    }
}
