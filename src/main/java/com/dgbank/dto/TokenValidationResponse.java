package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// Per validazione token
@Schema(description = "Response per validazione token")
public class TokenValidationResponse {
    @Schema(description = "Token valido", example = "true")
    private boolean valid;
    
    @Schema(description = "Email associata al token", example = "mario.rossi@email.com")
    private String email;
    
    // Costruttori
    public TokenValidationResponse() {}
    
    public TokenValidationResponse(boolean valid, String email) {
        this.valid = valid;
        this.email = email;
    }
    
    // Getter e setter
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
