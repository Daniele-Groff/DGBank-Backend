package com.dgbank.dto;
import io.swagger.v3.oas.annotations.media.Schema;

// DTO per login
@Schema(description = "Credenziali di login")
public class LoginRequest {
    @Schema(description = "Email dell'utente", example = "mario.rossi@email.com", required = true)
    private String email;
    @Schema(description = "Password dell'utente", example = "password123", required = true)
    private String password;
    
    // Getter e setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
