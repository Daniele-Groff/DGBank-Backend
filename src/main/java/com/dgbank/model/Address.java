package com.dgbank.model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
@Schema(description = "Entità indirizzo")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID univoco dell'indirizzo", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Via e numero civico", example = "Via Roma 15")
    private String street;
    
    @Column(nullable = false, length = 100)
    @Schema(description = "Città", example = "Milano")
    private String city;
    
    @Column(name = "postal_code", nullable = false, length = 10)
    @Schema(description = "CAP", example = "20121")
    private String postalCode;
    
    @Column(nullable = false, length = 50)
    @Schema(description = "Provincia", example = "MI")
    private String province;
    
    @Column(nullable = false, length = 50)
    @Schema(description = "Paese", example = "Italia")
    private String country;

    // Getter e setter
    public Long getId() { return id; }

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

    // Restituisce una stringa con l'indirizzo completo
    public String getFullAddress() {
        return String.format("%s, %s %s (%s), %s", 
            street, postalCode, city, province, country);
    }
}
