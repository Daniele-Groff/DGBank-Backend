package com.dgbank.dto;
import com.dgbank.model.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

// DTO per creazione utente
@Schema(description = "Dati per la registrazione di un nuovo utente")
public class RegisterRequest {
    // Campi per utente
    @Schema(description = "Email dell'utente", example = "mario.rossi@email.com", required = true)
    private String email;
    
    @Schema(description = "Password dell'utente", example = "password123", required = true, minLength = 8)
    private String password;
    
    @Schema(description = "Nome", example = "Mario", required = true)
    private String firstName;
    
    @Schema(description = "Cognome", example = "Rossi", required = true)
    private String lastName;
    
    @Schema(description = "Data di nascita (deve essere maggiorenne)", example = "1990-01-15", required = true)
    private LocalDate dateOfBirth;
    
    @Schema(description = "Numero di telefono", example = "+39 123 456 7890", required = true)
    private String phoneNumber;

    // Campi per Document
    @Schema(description = "Tipo di documento", example = "CARTA_IDENTITA", required = true)
    private DocumentType documentType;
    
    @Schema(description = "Numero del documento", example = "AB1234567", required = true)
    private String documentNumber;
    
    @Schema(description = "Data di scadenza del documento", example = "2030-01-15", required = true)
    private LocalDate documentExpiry;
    
    @Schema(description = "Ente che ha rilasciato il documento", example = "Comune di Milano", required = true)
    private String documentIssuer;
    
    // Campi per Address
    @Schema(description = "Indirizzo (via e numero civico)", example = "Via Roma 123", required = true)
    private String street;
    
    @Schema(description = "Città", example = "Milano", required = true)
    private String city;
    
    @Schema(description = "Codice postale", example = "20100", required = true)
    private String postalCode;
    
    @Schema(description = "Provincia", example = "MI", required = true)
    private String province;

    @Schema(description = "Paese", example = "Italia", required = true)
    private String country;

    // Getter e setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    
    public LocalDate getDocumentExpiry() { return documentExpiry; }
    public void setDocumentExpiry(LocalDate documentExpiry) { this.documentExpiry = documentExpiry; }
    
    public String getDocumentIssuer() { return documentIssuer; }
    public void setDocumentIssuer(String documentIssuer) { this.documentIssuer = documentIssuer; }
    
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
