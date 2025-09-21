package com.dgbank.dto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response base per tutte le operazioni")
public class BaseResponse {
    @Schema(description = "Indica se l'operazione è andata a buon fine", example = "true")
    private boolean success;
    
    @Schema(description = "Messaggio descrittivo del risultato", example = "Operazione completata con successo")
    private String message;
    
    // Costruttori
    public BaseResponse() {}
    
    public BaseResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getter e setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
