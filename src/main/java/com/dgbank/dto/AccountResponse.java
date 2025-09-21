package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// Per operazioni singole sui conti
@Schema(description = "Response per operazioni sui conti")
public class AccountResponse extends BaseResponse {
    @Schema(description = "Dati del conto")
    private AccountData account;
    
    // Costruttori
    public AccountResponse() {}
    
    public AccountResponse(boolean success, String message, AccountData account) {
        super(success, message);
        this.account = account;
    }
    
    // Getter e setter
    public AccountData getAccount() { return account; }
    public void setAccount(AccountData account) { this.account = account; }
}