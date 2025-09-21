package com.dgbank.controller;

import com.dgbank.dto.BaseResponse;
import com.dgbank.dto.CardData;
import com.dgbank.dto.CardListResponse;
import com.dgbank.dto.CardResponse;
import com.dgbank.dto.NewCardRequest;
import com.dgbank.model.Card;
import com.dgbank.service.CardService;
import com.dgbank.service.AuthService;
import com.dgbank.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cards", description = "Endpoint per la gestione delle carte")
@RestController
@RequestMapping("/cards")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:5500"})
public class CardController {
    
    @Autowired
    private CardService cardService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AuthService authService;

    @Operation(summary = "Rilascia carta", description = "Rilascia una carta associata ad un conto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carta creata con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi"),
        @ApiResponse(responseCode = "403", description = "Operazione non autorizzata"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/issue")
    public ResponseEntity<CardResponse> issueCard(@RequestBody NewCardRequest request) {
        validationService.validateId(request.getAccountId(), "account");

        authService.validateAccountAccess(request.getAccountId()); 
        
        Card newCard = cardService.issueCard(request.getAccountId());
            
        CardResponse response = new CardResponse(
            true, 
            "Carta creata con successo", 
            new CardData(newCard)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Trova carta", description = "Trova i dati di una carta tramite ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dati carta recuperati"),
        @ApiResponse(responseCode = "400", description = "ID carta non valido"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Carta non trovata"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long cardId) {
        validationService.validateId(cardId, "carta");
        
        authService.validateCardAccess(cardId);
        
        Card card = cardService.findById(cardId);
        
        CardResponse response = new CardResponse(
            true, 
            "Dati carta recuperati", 
            new CardData(card)
        );

        return ResponseEntity.ok(response);
    }

    // Ottieni tutte le carte di un utente
    @Operation(summary = "Ottieni carte utente", description = "Recupera tutte le carte di un utente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carte utente recuperate"),
        @ApiResponse(responseCode = "400", description = "ID utente non valido"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Utente non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CardListResponse> getUserCards(@PathVariable Long userId) {
        validationService.validateId(userId, "utente");
        
        // Controllo autorizzazione
        authService.validateUserAccess(userId);
        
        List<Card> cards = cardService.findCardsByUserId(userId);
        
        List<CardData> cardDataList = cards.stream()
            .map(CardData::new)
            .collect(Collectors.toList());
        
        CardListResponse response = new CardListResponse(true, cardDataList);
        response.setMessage("Carte utente recuperate con successo");
        
        return ResponseEntity.ok(response);
    }

    // Ottieni tutte le carte di un conto
    @Operation(summary = "Ottieni carte account", description = "Recupera tutte le carte collegate a un account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carte account recuperate"),
        @ApiResponse(responseCode = "400", description = "ID account non valido"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<CardListResponse> getAccountCards(@PathVariable Long accountId) {
        validationService.validateId(accountId, "account");
        
        // Controllo autorizzazione
        authService.validateAccountAccess(accountId);
        
        List<Card> cards = cardService.findCardsByLinkedAccountId(accountId);
        
        List<CardData> cardDataList = cards.stream()
            .map(CardData::new)
            .collect(Collectors.toList());
        
        CardListResponse response = new CardListResponse(true, cardDataList);
        response.setMessage("Carte account recuperate con successo");
        
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Blocca carta", description = "Disattiva temporaneamente una carta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carta bloccata con successo"),
        @ApiResponse(responseCode = "400", description = "ID carta non valido o carta già bloccata"),
        @ApiResponse(responseCode = "403", description = "Operazione non autorizzata"),
        @ApiResponse(responseCode = "404", description = "Carta non trovata"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PutMapping("/{cardId}/block")
    public ResponseEntity<BaseResponse> blockCard(@PathVariable Long cardId) {
        validationService.validateId(cardId, "carta");
        
        authService.validateCardAccess(cardId);
        
        cardService.blockCard(cardId);
        return ResponseEntity.ok(new BaseResponse(true, "Carta bloccata con successo"));
    }

    // Attiva carta
@Operation(summary = "Attiva carta", description = "Riattiva una carta precedentemente bloccata")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carta attivata con successo"),
        @ApiResponse(responseCode = "400", description = "ID carta non valido o carta già attiva"),
        @ApiResponse(responseCode = "403", description = "Operazione non autorizzata"),
        @ApiResponse(responseCode = "404", description = "Carta non trovata"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PutMapping("/{cardId}/activate")
    public ResponseEntity<BaseResponse> activateCard(@PathVariable Long cardId) {
        validationService.validateId(cardId, "carta");
        
        // Controllo autorizzazione
        authService.validateCardAccess(cardId);
        
        cardService.activateCard(cardId);
        return ResponseEntity.ok(new BaseResponse(true, "Carta attivata con successo"));
    }
}
