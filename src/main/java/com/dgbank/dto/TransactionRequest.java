package com.dgbank.dto;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

// DTO per transazioni da/a conti esterni
@Schema(description = "Dati per la richiesta di una transazione a enti esterni")
public class TransactionRequest {
    @Schema(description = "ID dell'account emittente o destinatario", example = "1", required = true)
    private Long accountId;
    @Schema(description = "Importo", example = "42.00", required = true)
    private BigDecimal amount;
    @Schema(description = "Descrizione o causale", example = "Rata affitto", required = true)
    private String description;
    
    // Getter e setter
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
