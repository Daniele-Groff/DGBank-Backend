package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

// Per lista conti utente
@Schema(description = "Response per lista conti di un utente")
public class UserAccountsResponse extends BaseResponse {
    @Schema(description = "Lista dei conti dell'utente")
    private List<AccountData> accounts;
    
    @Schema(description = "Saldo totale di tutti i conti", example = "3500.75")
    private BigDecimal totalBalance;
    
    // Costruttori
    public UserAccountsResponse() {}
    
    public UserAccountsResponse(boolean success, List<AccountData> accounts, BigDecimal totalBalance) {
        super(success, "Dati degli account recuperati");
        this.accounts = accounts;
        this.totalBalance = totalBalance;
    }
    
    // Getter e setter
    public List<AccountData> getAccounts() { return accounts; }
    public void setAccounts(List<AccountData> accounts) { this.accounts = accounts; }
    
    public BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
}

