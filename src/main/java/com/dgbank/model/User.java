package com.dgbank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "users")
@Schema(description = "Entità utente della banca")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID univoco dell'utente", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "Email e credenziale di accesso dell'utente", example = "mario.rossi@email.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Password criptata", hidden = true)
    private String password; 

    @Column(name = "first_name", nullable = false, length = 50)
    @Schema(description = "Nome dell'utente", example = "Mario")
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    @Schema(description = "Cognome dell'utente", example = "Rossi")
    private String lastName;
    
    @Column(name = "date_of_birth", nullable = false)
    @Schema(description = "Data di nascita", example = "1990-01-15")
    private LocalDate dateOfBirth;
    
    @Column(name = "phone_number", nullable = false, length = 20)
    @Schema(description = "Numero di telefono", example = "+39 123 456 7890")
    private String phoneNumber;

    @Column(name = "is_active")
    @Schema(description = "Indica se l'utente è attivo", example = "true")
    private Boolean isActive = true;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id", nullable = false)
    @Schema(description = "Documento di identità dell'utente")
    private Document document;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", nullable = false)
    @Schema(description = "Indirizzo di residenza dell'utente")
    private Address address;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Lista dei conti bancari dell'utente")
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @Schema(description = "Lista delle carte dell'utente")
    private List<Account> cards = new ArrayList<>();
    
    @Column(name = "date_created", updatable = false)
    @Schema(description = "Data di registrazione", example = "2024-01-15T10:30:00")
    private LocalDateTime dateCreated;

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
    }

    // Getter e setter
    public Long getId(){ return id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Boolean getIsActive() { return isActive; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    // Calcolo dell'età sulla base della data corrente
    private int calculateAge() {
        if (dateOfBirth != null) {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        }
        return 0;
    }

    // Verifica che il proprietario sia maggiorenne
    public boolean isAdult() {
        return calculateAge() >= 18;
    }

    // Restituisce stringa con nome e cognome
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Verifica validità del documento
    public boolean validateIdentity() {
        return document != null && !document.isExpired();
    }
}