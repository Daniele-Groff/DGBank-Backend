package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response per refresh del token")
public class RefreshTokenResponse extends BaseResponse {
    @Schema(description = "Nuovo token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Token per refresh (stesso di prima)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    // Costruttori
    public RefreshTokenResponse() {}
    
    public RefreshTokenResponse(boolean success, String token, String refreshToken) {
        super(success, "Token refresh completato");
        this.token = token;
        this.refreshToken = refreshToken;
    }
    
    public RefreshTokenResponse(boolean success, String message, String token, String refreshToken) {
        super(success, message);
        this.token = token;
        this.refreshToken = refreshToken;
    }
    
    // Getter e setter
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}