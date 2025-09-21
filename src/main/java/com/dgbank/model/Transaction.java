package com.dgbank.model;
import com.dgbank.model.enums.TransactionStatus;
import com.dgbank.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Schema(description = "Entità delle transazioni")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID univoco della transazione", example = "1")
    private Long id;
    
    @Column(name = "transaction_id", nullable = false, unique = true, length = 50)
    @Schema(description = "Numero identificativo della transazione", example = "4559ea59-73c4-4f52-a446-748d05ef6239")
    private String transactionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", nullable = false)
    @Schema(description = "Account emittente")
    private Account fromAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", nullable = false)
    @Schema(description = "Account ricevente")
    private Account toAccount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    @Schema(description = "Importo", example = "42.00")
    private BigDecimal amount;
    
    @Column(name = "description", length = 255)
    @Schema(description = "Descrizione / Causale", example = "Rata affitto")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Tipo", example = "TRANSFER")
    private TransactionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Stato", example = "COMPLETED")
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(nullable = false, updatable = false)
    @Schema(description = "Data e ora", example = "2025-09-13 17:53:05.979981")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }
    }

    // Getter e setter
    public Long getId() { return id; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Account getFromAccount() { return fromAccount; }
    public void setFromAccount(Account fromAccount) { this.fromAccount = fromAccount; }

    public Account getToAccount() { return toAccount; }
    public void setToAccount(Account toAccount) { this.toAccount = toAccount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Esegue transazione
    public void execute() {
        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalStateException("La transazione non è in stato PENDING");
        }
        
        try {
            // Esegui il trasferimento
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            this.status = TransactionStatus.COMPLETED;
        } catch (Exception e) {
            this.status = TransactionStatus.FAILED;
            throw e;
        }
    }
    
    // Cancella transazione, se ancora in sospeso
    public void cancel() {
        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Solo transazioni PENDING possono essere cancellate");
        }
        this.status = TransactionStatus.CANCELLED;
    }
    
    // Storna la transazione, se già completata
    public void rollback() {
        if (this.status != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Solo transazioni COMPLETED possono essere annullate");
        }
        
        // Inverti il trasferimento
        toAccount.withdraw(amount);
        fromAccount.deposit(amount);
        this.status = TransactionStatus.CANCELLED;
    }
}
