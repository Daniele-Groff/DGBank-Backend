package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

// Per lista transazioni
@Schema(description = "Response per lista transazioni")
public class TransactionListResponse extends BaseResponse {
    @Schema(description = "ID account/utente", example = "1")
    private Long accountId;
    
    @Schema(description = "Lista delle transazioni")
    private List<TransactionData> transactions;
    
    @Schema(description = "Numero di transazioni", example = "15")
    private int count;
    
    public TransactionListResponse() {}
    
    public TransactionListResponse(boolean success, Long accountId, List<TransactionData> transactions) {
        super(success, "Operazione completata");
        this.accountId = accountId;
        this.transactions = transactions;
        this.count = transactions != null ? transactions.size() : 0;
    }
    
    // Getter e setter
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public List<TransactionData> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionData> transactions) { 
        this.transactions = transactions;
        this.count = transactions != null ? transactions.size() : 0;
    }
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
