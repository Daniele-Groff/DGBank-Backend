package com.dgbank.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;


@Entity
@Table(name = "accounts")
@Schema(description = "Entità conto bancario")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID univoco del conto", example = "1")
    private Long id;
    
    @Column(name = "account_number", nullable = false, unique = true, length = 34)
    @Schema(description = "Numero IBAN del conto", example = "IT 12 X 05472 81110 123456789012")
    private String accountNumber;

    @Column(nullable = false, precision = 15, scale = 2)
    @Schema(description = "Saldo disponibile", example = "1500.42")
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "is_active")
    @Schema(description = "Indica se il conto è attivo", example = "true")
    private Boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @Schema(description = "Proprietario del conto")
    private User owner;

    @OneToMany(mappedBy = "linkedAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Carte associate al conto")
    private List<Card> cards = new ArrayList<>();

    @Column(name = "date_opened", updatable = false)
    @Schema(description = "Data di apertura del conto", example = "2024-01-15T10:30:00")
    private LocalDateTime dateOpened;

    @PrePersist
    protected void onCreate() {
        dateOpened = LocalDateTime.now();
    }

    // Getter e setter
    public Long getId(){ return id; }

    public String getAccountNumber(){ return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getBalance() { return balance; }

    public Boolean getIsActive() { return isActive; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDateTime getDateOpened() { return dateOpened; }

    // Deposito fondi
    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    
    // Prelievo fondi
    public void withdraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
    
    // Congela conto
    public void freeze() {
        this.isActive = false;
    }
    
    // Scongela conto
    public void unfreeze() {
        this.isActive = true;
    }
}
