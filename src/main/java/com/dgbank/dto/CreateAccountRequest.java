package com.dgbank.dto;
import io.swagger.v3.oas.annotations.media.Schema;

// DTO per creazione account
@Schema(description = "Dati per la creazione di un nuovo account")
public class CreateAccountRequest {
    @Schema(description = "ID dell'utente intestatario", example = "1", required = true)
    private Long userId;

    // Getter e setter
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
