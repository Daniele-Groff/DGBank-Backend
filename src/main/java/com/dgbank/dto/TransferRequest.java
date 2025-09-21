package com.dgbank.dto;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

// DTO per transazioni tra conti interni
@Schema(description = "Dati per richiedere un trasferimento tra conti")
public class TransferRequest {
    @Schema(description = "ID dell'account emittente", example = "1", required = true)
    private Long fromAccountId;
    @Schema(description = "IBAN dell'account destinatario", example = "IT 36 X 05472 81110 000664209461", required = true)
    private String toAccountIban;
    @Schema(description = "Importo", example = "42.00", required = true)
    private BigDecimal amount;
    @Schema(description = "Descrizione o causale", example = "Rata affitto", required = true)
    private String description;
    
    // Getter e setter
    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
    
    public String getToAccountIban() { return toAccountIban; }
    public void setToAccountIban(String toAccountIban) { this.toAccountIban = toAccountIban; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
