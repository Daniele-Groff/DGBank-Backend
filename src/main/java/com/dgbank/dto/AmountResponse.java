package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Per entrate e spese
@Schema(description = "Response per somme di importi")
public class AmountResponse extends BaseResponse {
    @Schema(description = "ID utente", example = "1")
    private Long userId;
    
    @Schema(description = "Data di inizio calcolo", example = "2024-01-15T00:00:00")
    private LocalDateTime sinceDate;
    
    @Schema(description = "Importo totale", example = "1500.00")
    private BigDecimal totalAmount;
    
    public AmountResponse() {}
    
    public AmountResponse(boolean success, Long userId, LocalDateTime sinceDate, BigDecimal totalAmount) {
        super(success, "Calcolo completato");
        this.userId = userId;
        this.sinceDate = sinceDate;
        this.totalAmount = totalAmount;
    }
    
    // Getter e setter
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public LocalDateTime getSinceDate() { return sinceDate; }
    public void setSinceDate(LocalDateTime sinceDate) { this.sinceDate = sinceDate; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
