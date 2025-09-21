package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.dgbank.model.Transaction;
import com.dgbank.model.enums.TransactionType;
import com.dgbank.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Dati della transazione
@Schema(description = "Dati della transazione")
public class TransactionData {
    @Schema(description = "ID della transazione", example = "1")
    private Long id;
    
    @Schema(description = "ID univoco della transazione", example = "550e8400-e29b-41d4-a716-446655440000")
    private String transactionId;
    
    @Schema(description = "Importo", example = "500.00")
    private BigDecimal amount;
    
    @Schema(description = "Descrizione", example = "Deposito contanti")
    private String description;
    
    @Schema(description = "Tipo di transazione")
    private TransactionType type;
    
    @Schema(description = "Stato della transazione")
    private TransactionStatus status;
    
    @Schema(description = "Data e ora della transazione", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Mittente")
    private String sender;
    
    @Schema(description = "Destinatario")
    private String recipient;
    
    // Costruttori
    public TransactionData() {}
    
    public TransactionData(Transaction transaction) {
        this.id = transaction.getId();
        this.transactionId = transaction.getTransactionId();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.type = transaction.getType();
        this.status = transaction.getStatus();
        this.timestamp = transaction.getTimestamp();

        if (transaction.getFromAccount() != null) {
            this.sender = transaction.getFromAccount().getOwner().getFullName();
        }
        if (transaction.getToAccount() != null) {
            this.recipient = transaction.getToAccount().getOwner().getFullName();
        }
    }
    
    // Getter e setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
}
