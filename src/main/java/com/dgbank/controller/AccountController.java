package com.dgbank.controller;

import com.dgbank.dto.*;
import com.dgbank.model.Account;
import com.dgbank.service.AccountService;
import com.dgbank.validation.ValidationService;
import com.dgbank.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;



@Tag(name = "Accounts", description = "Endpoint per la gestione degi conti")
@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:5500"})
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private ValidationService validationService;

    @Autowired
    private AuthService authService;

    // Crea un nuovo conto per un utente
    @Operation(summary = "Crea account", description = "Crea un nuovo account per un utente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Conto creato con successo"),
        @ApiResponse(responseCode = "400", description = "ID utente non valido"),
        @ApiResponse(responseCode = "403", description = "Utente non autorizzato"),
        @ApiResponse(responseCode = "404", description = "Utente non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        validationService.validateId(request.getUserId(), "utente");

        // Controllo autorizzazione: può creare account solo per se stesso
        // Genera UnauthorizedException
        authService.validateUserAccess(request.getUserId());

        Account newAccount = accountService.createAccount(request.getUserId());
        AccountResponse response = new AccountResponse(
            true, 
            "Conto creato con successo", 
            new AccountData(newAccount)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Ottieni dettagli da un conto specifico
    @Operation(summary = "Trova dati account", description = "Trova i dati di un account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dati account recuperati"),
        @ApiResponse(responseCode = "400", description = "ID account non valido"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        validationService.validateId(accountId, "account");
        
        // Può accedere solo il proprietario
        authService.validateAccountAccess(accountId);

        Account account = accountService.findById(accountId);
        AccountResponse response = new AccountResponse(
            true, 
            "Dati recuperati con successo", 
            new AccountData(account)
        );
            
        return ResponseEntity.ok(response);
    }

    // Ottieni il saldo di un conto
    @Operation(summary = "Verifica saldo", description = "Verifica il saldo di un account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saldo recuperato con successo"),
        @ApiResponse(responseCode = "400", description = "ID account non valido"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long accountId) {
        // Per controllo bad request
        validationService.validateId(accountId, "account");
        
        // Per controllo unauthorized
        authService.validateAccountAccess(accountId);

        // Controlli not found
        BigDecimal balance = accountService.getBalance(accountId);

        BalanceResponse response = new BalanceResponse(true, accountId, balance);
        return ResponseEntity.ok(response);
    }

    // Congela un conto
    @Operation(summary = "Congela account", description = "Congela un conto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conto congelato con successo"),
        @ApiResponse(responseCode = "400", description = "ID account non valido o conto già congelato"),
        @ApiResponse(responseCode = "403", description = "Operazione non autorizzata"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<BaseResponse> freezeAccount(@PathVariable Long accountId) {
        validationService.validateId(accountId, "account");
        
        // Controllo autorizzazione
        authService.validateAccountAccess(accountId);
        
        accountService.freezeAccount(accountId);
        return ResponseEntity.ok(new BaseResponse(true, "Conto congelato con successo"));
    }

    // Scongela un conto
    @Operation(summary = "Scongela account", description = "Scongela un conto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conto scongelato con successo"),
        @ApiResponse(responseCode = "400", description = "ID account non valido o conto già attivo"),
        @ApiResponse(responseCode = "403", description = "Operazione non autorizzata"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PutMapping("/{accountId}/unfreeze")
    public ResponseEntity<BaseResponse> unfreezeAccount(@PathVariable Long accountId) {
        validationService.validateId(accountId, "account");
        
        // Controllo autorizzazione
        authService.validateAccountAccess(accountId);
        
        accountService.unfreezeAccount(accountId);
        return ResponseEntity.ok(new BaseResponse(true, "Conto scongelato con successo"));
    }


    // Ottieni tutti i conti di un utente
    @Operation(summary = "Trova account utente", description = "Trova tutti gli account di un utente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conti utente recuperati"),
        @ApiResponse(responseCode = "400", description = "ID utente non valido"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Utente non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAccountsResponse> getUserAccounts(@PathVariable Long userId) {
        validationService.validateId(userId, "utente");
            
        authService.validateUserAccess(userId);

        List<Account> accounts = accountService.findAccountsByUserId(userId);
        BigDecimal totalBalance = accountService.getTotalBalance(userId);
        
        List<AccountData> accountDataList = accounts.stream()
            .map(AccountData::new)
            .collect(Collectors.toList());
        
        UserAccountsResponse response = new UserAccountsResponse(
            true, 
            accountDataList, 
            totalBalance
        );
        response.setMessage("Conti utente recuperati con successo");
        
        return ResponseEntity.ok(response);
    } 
}
