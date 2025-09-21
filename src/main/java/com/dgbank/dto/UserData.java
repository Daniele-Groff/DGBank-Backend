package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.dgbank.model.User;
import java.time.LocalDate;

@Schema(description = "Dati utente per le response")
public class UserData {
    @Schema(description = "ID utente", example = "1")
    private Long id;
    
    @Schema(description = "Email", example = "mario.rossi@email.com")
    private String email;
    
    @Schema(description = "Nome", example = "Mario")
    private String firstName;
    
    @Schema(description = "Cognome", example = "Rossi")
    private String lastName;
    
    @Schema(description = "Nome completo", example = "Mario Rossi")
    private String fullName;
    
    @Schema(description = "Data di nascita", example = "1990-01-15")
    private LocalDate dateOfBirth;
    
    @Schema(description = "Indirizzo completo", example = "Via Roma 123, 20100 Milano (MI), Italia")
    private String fullAddress;
    
    @Schema(description = "Tipo documento registrato", example = "Carta d'Identità")
    private String registeredDocument;
    
    // Costruttori
    public UserData() {}
    
    public UserData(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
        this.dateOfBirth = user.getDateOfBirth();
        if (user.getAddress() != null) {
            this.fullAddress = user.getAddress().getFullAddress();
        }
        if (user.getDocument() != null) {
            this.registeredDocument = user.getDocument().getType().getDisplayName();
        }
    }
    
    // Getter e setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    
    public String getRegisteredDocument() { return registeredDocument; }
    public void setRegisteredDocument(String registeredDocument) { this.registeredDocument = registeredDocument; }
}