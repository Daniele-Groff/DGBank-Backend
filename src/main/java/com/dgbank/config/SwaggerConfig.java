package com.dgbank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import java.util.LinkedHashMap;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DG Bank API")
                .version("1.0")
                .description("API REST per il sistema bancario DG Bank. Consente gestione utenti, conti, carte e transazioni.")
                .contact(new Contact()
                    .name("Daniele Groff")
                    .email("groff.daniele@gmail.com")
                    .url("https://github.com/your-username/dgbank")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Inserire il token JWT ottenuto dal login")));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Aggiungi descrizioni personalizzate per gli schemi delle entità
            if (openApi.getComponents() != null && openApi.getComponents().getSchemas() != null) {
                Map<String, Schema> schemas = openApi.getComponents().getSchemas();
                
                // Aggiungi descrizioni per ogni schema
                addSchemaDescription(schemas, "User", "Rappresenta un utente del sistema bancario");
                addSchemaDescription(schemas, "Account", "Rappresenta un conto bancario");
                addSchemaDescription(schemas, "Card", "Rappresenta una carta di pagamento");
                addSchemaDescription(schemas, "Transaction", "Rappresenta una transazione bancaria");
                addSchemaDescription(schemas, "Document", "Rappresenta un documento di identità");
                addSchemaDescription(schemas, "Address", "Rappresenta un indirizzo di residenza");
                addSchemaDescription(schemas, "DocumentType", "Enum dei tipi di documento accettati");
                addSchemaDescription(schemas, "TransactionType", "Enum dei tipi di transazione");
                addSchemaDescription(schemas, "TransactionStatus", "Enum degli stati delle transazioni");
                
                // Riordina gli schemi per avere le entità principali per prime
                Map<String, Schema> orderedSchemas = new LinkedHashMap<>();
                
                // Prima le entità principali
                moveSchema(schemas, orderedSchemas, "User");
                moveSchema(schemas, orderedSchemas, "Account");
                moveSchema(schemas, orderedSchemas, "Card");
                moveSchema(schemas, orderedSchemas, "Transaction");
                moveSchema(schemas, orderedSchemas, "Document");
                moveSchema(schemas, orderedSchemas, "Address");
                
                // Poi gli enum
                moveSchema(schemas, orderedSchemas, "DocumentType");
                moveSchema(schemas, orderedSchemas, "TransactionType");
                moveSchema(schemas, orderedSchemas, "TransactionStatus");
                
                // Aggiungi tutti gli altri schemi rimanenti (DTO, etc.)
                orderedSchemas.putAll(schemas);
                
                // Sostituisci con la mappa ordinata
                openApi.getComponents().setSchemas(orderedSchemas);
            }
        };
    }
    
    private void addSchemaDescription(Map<String, Schema> schemas, String schemaName, String description) {
        if (schemas.containsKey(schemaName)) {
            schemas.get(schemaName).description(description);
        }
    }
    
    private void moveSchema(Map<String, Schema> source, Map<String, Schema> destination, String key) {
        if (source.containsKey(key)) {
            destination.put(key, source.remove(key));
        }
    }
}