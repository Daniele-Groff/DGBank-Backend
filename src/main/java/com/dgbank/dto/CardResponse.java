package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// Per operazioni sulle carte
@Schema(description = "Response per operazioni sulle carte")
public class CardResponse extends BaseResponse {
    @Schema(description = "Dati della carta")
    private CardData card;
    
    public CardResponse() {}
    
    public CardResponse(boolean success, String message, CardData card) {
        super(success, message);
        this.card = card;
    }
    
    public CardData getCard() { return card; }
    public void setCard(CardData card) { this.card = card; }
}