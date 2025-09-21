package com.dgbank.dto;
import io.swagger.v3.oas.annotations.media.Schema;

// DTO per la richiesta di refresh o del token
@Schema(description = "Dati per richiedere il token")
public class RefreshTokenRequest {
    @Schema(description = "Refresh token", required = true)
    private String refreshToken;
    
    // Getter e setter
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
