package com.dgbank.dto;
import io.swagger.v3.oas.annotations.media.Schema;

// DTO per rilascio carta
@Schema(description = "Dati per il rilascio di una nuova carta")
public class NewCardRequest {
    @Schema(description = "ID dell'account a cui associare la carta", example = "1", required = true)
    private Long accountId;

    // Getter e setter
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
}
