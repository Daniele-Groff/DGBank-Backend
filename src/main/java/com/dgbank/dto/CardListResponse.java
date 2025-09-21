package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

// Per lista carte
@Schema(description = "Response per lista carte")
public class CardListResponse extends BaseResponse {
    @Schema(description = "Lista delle carte")
    private List<CardData> cards;
    
    public CardListResponse() {}
    
    public CardListResponse(boolean success, List<CardData> cards) {
        super(success, "Operazione completata");
        this.cards = cards;
    }
    
    public List<CardData> getCards() { return cards; }
    public void setCards(List<CardData> cards) { this.cards = cards; }
}

