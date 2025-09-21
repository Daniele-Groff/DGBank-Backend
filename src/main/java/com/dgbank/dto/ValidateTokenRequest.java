package com.dgbank.dto;
import io.swagger.v3.oas.annotations.media.Schema;

// DTO per validazione del token
@Schema(description = "Dati per verificare la validità del token")
public class ValidateTokenRequest {
    @Schema(description = "Token", required = true)
    private String token;
    
    // Getter e setter
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
