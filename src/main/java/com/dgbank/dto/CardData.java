package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.dgbank.model.Card;
import java.time.LocalDate;


// CardData.java - Dati della carta
@Schema(description = "Dati della carta")
public class CardData {
    @Schema(description = "ID della carta", example = "1")
    private Long id;
    
    @Schema(description = "Numero carta mascherato", example = "**** **** **** 1234")
    private String cardNumber;
    
    @Schema(description = "Data scadenza", example = "2028-01-15")
    private LocalDate expiryDate;
    
    @Schema(description = "Carta attiva", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Proprietario della carta", example = "Mario Rossi")
    private String owner;
    
    @Schema(description = "Numero account collegato", example = "IT 12 X 05472 81110 123456789012")
    private String accountNumber;
    
    // Costruttori
    public CardData() {}
    
    public CardData(Card card) {
        this.id = card.getId();
        this.cardNumber = card.getMaskedCardNumber();
        this.expiryDate = card.getExpiryDate();
        this.isActive = card.getIsActive();
        
        if (card.getOwner() != null) {
            this.owner = card.getOwner().getFullName();
        }
        if (card.getLinkedAccount() != null) {
            this.accountNumber = card.getLinkedAccount().getAccountNumber();
        }
    }
    
    // Getter e setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
}
