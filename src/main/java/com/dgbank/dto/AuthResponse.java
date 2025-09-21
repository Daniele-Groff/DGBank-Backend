package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// Response per login e registrazione
@Schema(description = "Response per operazioni di autenticazione")
public class AuthResponse extends BaseResponse {
    @Schema(description = "Token JWT per l'autenticazione", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Token per refresh", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    @Schema(description = "Dati dell'utente autenticato")
    private UserData user;
    
    // Costruttori
    public AuthResponse() {}
    
    public AuthResponse(boolean success, String message, String token, String refreshToken, UserData user) {
        super(success, message);
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }
    
    // Getter e setter
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    
    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }
}