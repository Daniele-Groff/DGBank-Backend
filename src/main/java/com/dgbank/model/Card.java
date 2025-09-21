package com.dgbank.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "cards")
@Schema(description = "Entità carta")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID univoco della carta", example = "1")
    private Long id;
    
    @Column(name = "card_number", nullable = false, unique = true, length = 19)
    @Schema(description = "Numero", example = "5025 2690 3055 2032")
    private String cardNumber;

    @Column(nullable = false, length = 4)
    @Schema(description = "CVV", example = "123")
    private String cvv;
    
    @Column(name = "expiry_date", nullable = false)
    @Schema(description = "Data di scadenza", example = "01-01-2028")
    private LocalDate expiryDate;

    @Column(name = "is_active")
    @Schema(description = "Stato di attività", example = "true")
    private Boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_account_id", nullable = false)
    @Schema(description = "Account collegato")
    private Account linkedAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @Schema(description = "Proprietario")
    private User owner;

    // Getter e setter
    public Long getId() { return id; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public Boolean getIsActive() { return isActive; }
    
    public Account getLinkedAccount() { return linkedAccount; }
    public void setLinkedAccount(Account linkedAccount) { this.linkedAccount = linkedAccount; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    
    // Sblocca carta
    public void activate() {
        this.isActive = true;
    }
    
    // Blocca carta
    public void block() {
        this.isActive = false;
    }
    
    // Verifica che la carta non sia scaduta
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
    
    // Restituisce il numero della carta con cifre occultate
    public String getMaskedCardNumber() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            String lastFour = cardNumber.substring(cardNumber.length() - 4);
            return "**** **** **** " + lastFour;
        }
        return "****";
    }
}
