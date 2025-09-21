package com.dgbank.dto;
import io.swagger.v3.oas.annotations.media.Schema;

// Response per operazioni singole sulle transazioni
@Schema(description = "Response per operazioni sulle transazioni")
public class TransactionResponse extends BaseResponse {
    @Schema(description = "Dati della transazione")
    private TransactionData transaction;
    
    public TransactionResponse() {}
    
    public TransactionResponse(boolean success, String message, TransactionData transaction) {
        super(success, message);
        this.transaction = transaction;
    }
    
    public TransactionData getTransaction() { return transaction; }
    public void setTransaction(TransactionData transaction) { this.transaction = transaction; }
}
