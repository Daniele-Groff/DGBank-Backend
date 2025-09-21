package com.dgbank.model.enums;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipi di documento accettati")
public enum DocumentType {
    @Schema(description = "Carta d'Identità")
    CARTA_IDENTITA("Carta d'Identità"),

    @Schema(description = "Passaporto")
    PASSAPORTO("Passaporto"),

    @Schema(description = "Patente di Guida")
    PATENTE("Patente di Guida"),

    @Schema(description = "Permesso di Soggiorno")
    PERMESSO_SOGGIORNO("Permesso di Soggiorno");

    private final String displayName;
    
    DocumentType(String displayName) {
        this.displayName = displayName;
    }
    
    // Per restituire un nome leggibile delle costanti
    public String getDisplayName() {
        return displayName;
    }
}
