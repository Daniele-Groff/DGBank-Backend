package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.dgbank.model.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dati del conto bancario")
public class AccountData {
    @Schema(description = "ID del conto", example = "1")
    private Long id;
    
    @Schema(description = "Numero IBAN del conto", example = "IT 12 X 05472 81110 123456789012")
    private String accountNumber;
    
    @Schema(description = "Saldo disponibile", example = "1500.50")
    private BigDecimal balance;
    
    @Schema(description = "Indica se il conto è attivo", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Data di apertura del conto", example = "2024-01-15T10:30:00")
    private LocalDateTime dateOpened;
    
    @Schema(description = "Informazioni sul proprietario")
    private OwnerInfo owner;
    
    // Costruttori
    public AccountData() {}
    
    public AccountData(Account account) {
        this.id = account.getId();
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.isActive = account.getIsActive();
        this.dateOpened = account.getDateOpened();
        if (account.getOwner() != null) {
            this.owner = new OwnerInfo(account.getOwner());
        }
    }
    
    // Getter e setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getDateOpened() { return dateOpened; }
    public void setDateOpened(LocalDateTime dateOpened) { this.dateOpened = dateOpened; }
    
    public OwnerInfo getOwner() { return owner; }
    public void setOwner(OwnerInfo owner) { this.owner = owner; }
    
    // Classe per informazioni base del proprietario
    @Schema(description = "Informazioni base del proprietario")
    public static class OwnerInfo {
        @Schema(description = "ID del proprietario", example = "1")
        private Long id;
        
        @Schema(description = "Nome completo", example = "Mario Rossi")
        private String fullName;
        
        @Schema(description = "Email", example = "mario.rossi@email.com")
        private String email;
        
        public OwnerInfo() {}
        
        public OwnerInfo(com.dgbank.model.User user) {
            this.id = user.getId();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
        }
        
        // Getter e setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}