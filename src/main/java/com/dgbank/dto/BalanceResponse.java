package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

// BalanceResponse.java - Per saldo conto
@Schema(description = "Response per richiesta saldo")
public class BalanceResponse extends BaseResponse {
    @Schema(description = "ID del conto", example = "1")
    private Long accountId;
    
    @Schema(description = "Saldo attuale", example = "1500.50")
    private BigDecimal balance;
    
    // Costruttori
    public BalanceResponse() {}
    
    public BalanceResponse(boolean success, Long accountId, BigDecimal balance) {
        super(success, "Saldo recuperato con successo");
        this.accountId = accountId;
        this.balance = balance;
    }
    
    // Getter e setter
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
