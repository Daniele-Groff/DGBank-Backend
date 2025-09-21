package com.dgbank.model;
import com.dgbank.model.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "documents")
@Schema(description = "Entità documento di identità")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID univoco del documento", example = "1")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Tipo di documento", example = "CARTA_IDENTITA")
    private DocumentType type;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "Numero", example = "AB1234567")
    private String number;

    @Column(name = "expiry_date", nullable = false)
    @Schema(description = "Data di scadenza", example = "2029-01-01")
    private LocalDate expiryDate;
    
    @Column(nullable = false, length = 100)
    @Schema(description = "Ente di rilascio", example = "Comune di Milano")
    private String issuer;

    // Getter e setter
    public Long getId() { return id; }

    public DocumentType getType() { return type; }
    public void setType(DocumentType type) { this.type = type; }
    
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    // Verifica se il documento è scaduto
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
}
