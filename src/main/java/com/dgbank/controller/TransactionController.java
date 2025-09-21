package com.dgbank.controller;

import com.dgbank.dto.*;
import com.dgbank.model.Transaction;
import com.dgbank.service.TransactionService;
import com.dgbank.validation.ValidationService;
import com.dgbank.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Transactions", description = "Endpoint per la gestione delle transazioni")
@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:5500"})
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AuthService authService;

    // Effettua un deposito
    @Operation(summary = "Effettua deposito", description = "Effettua un deposito")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Deposito completato con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi o importo non positivo"),
        @ApiResponse(responseCode = "403", description = "Operazione non consentita"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest request) {
        validationService.validateId(request.getAccountId(), "account");
        validationService.validateAmount(request.getAmount());

        authService.validateAccountAccess(request.getAccountId());

        Transaction transaction = transactionService.createDeposit(
            request.getAccountId(),
            request.getAmount(),
            request.getDescription()
        );
        
        TransactionResponse response = new TransactionResponse(
            true,
            "Deposito completato con successo",
            new TransactionData(transaction)
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Effettua un prelievo
    @Operation(summary = "Effettua prelievo", description = "Preleva denaro da un conto (es. da ATM)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Prelievo completato con successo"),
        @ApiResponse(responseCode = "400", description = "Saldo insufficiente o dati non validi"),
        @ApiResponse(responseCode = "403", description = "Operazione non consentita"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest request) {
        validationService.validateId(request.getAccountId(), "account");
        validationService.validateAmount(request.getAmount());
        
        // Controllo autorizzazione
        authService.validateAccountAccess(request.getAccountId());
        
        Transaction transaction = transactionService.createWithdrawal(
            request.getAccountId(),
            request.getAmount(),
            request.getDescription()
        );
        
        TransactionResponse response = new TransactionResponse(
            true,
            "Prelievo completato con successo",
            new TransactionData(transaction)
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Effettua trasferimento", description = "Trasferisce denaro tra due conti interni")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Trasferimento completato con successo"),
        @ApiResponse(responseCode = "400", description = "Saldo insufficiente o dati non validi"),
        @ApiResponse(responseCode = "403", description = "Operazione non consentita"),
        @ApiResponse(responseCode = "404", description = "Uno dei conti non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransferRequest request) {
        // Controllo dato mittente
        validationService.validateId(request.getFromAccountId(), "account mittente");

        // Controllo token
        authService.validateAccountAccess(request.getFromAccountId());

        // Controllo altri dati
        validationService.validateIban(request.getToAccountIban());
        validationService.validateAmount(request.getAmount());

        // Crea il trasferimento
        Transaction transaction = transactionService.createTransfer(
            request.getFromAccountId(),
            request.getToAccountIban(),
            request.getAmount(),
            request.getDescription()
        );
        
        // Genera response
        TransactionResponse response = new TransactionResponse(
            true,
            "Trasferimento completato con successo",
            new TransactionData(transaction)
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Ottieni le transazioni di un conto
    @Operation(summary = "Ottieni transazioni account", description = "Recupera tutte le transazioni di un conto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transazioni recuperate con successo"),
        @ApiResponse(responseCode = "400", description = "ID account non valido"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<TransactionListResponse> getAccountTransactions(@PathVariable Long accountId) {
        validationService.validateId(accountId, "account");
        
        // Controllo autorizzazione
        authService.validateAccountAccess(accountId);
        
        List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
        
        List<TransactionData> transactionDataList = transactions.stream()
            .map(TransactionData::new)
            .collect(Collectors.toList());
        
        TransactionListResponse response = new TransactionListResponse(
            true, 
            accountId, 
            transactionDataList
        );
        
        return ResponseEntity.ok(response);
    }

    // Ottieni le transazioni di un conto con paginazione
    @Operation(summary = "Ottieni transazioni paginate", description = "Recupera le transazioni di un conto con paginazione")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transazioni paginate recuperate con successo"),
        @ApiResponse(responseCode = "400", description = "Parametri di paginazione non validi"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/account/{accountId}/paginated")
    public ResponseEntity<PaginatedTransactionResponse> getAccountTransactionsPaginated(
            @PathVariable Long accountId,
            @Parameter(description = "Numero pagina", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Elementi per pagina", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        validationService.validateId(accountId, "account");
        validationService.validatePagination(page, size);
        
        // Controllo autorizzazione
        authService.validateAccountAccess(accountId);
        
        Page<Transaction> transactionPage = transactionService
            .getTransactionsByAccountPaginated(accountId, page, size);
        
        List<TransactionData> transactionDataList = transactionPage.getContent().stream()
            .map(TransactionData::new)
            .collect(Collectors.toList());
        
        PaginatedTransactionResponse response = new PaginatedTransactionResponse(
            true,
            accountId,
            transactionDataList,
            page,
            transactionPage.getTotalPages(),
            transactionPage.getTotalElements(),
            transactionPage.hasNext(),
            transactionPage.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }

    // Ottieni le ultime transazioni di un account
    @Operation(summary = "Ottieni transazioni recenti account", description = "Recupera le ultime N transazioni di un conto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transazioni recenti recuperate con successo"),
        @ApiResponse(responseCode = "400", description = "Parametro limit non valido"),
        @ApiResponse(responseCode = "404", description = "Account non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/account/{accountId}/recent")
    public ResponseEntity<TransactionListResponse> getRecentAccountTransactions(
            @PathVariable Long accountId,
            @Parameter(description = "Numero massimo di transazioni", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        validationService.validateId(accountId, "account");
        validationService.validateLimit(limit);
        
        // Controllo autorizzazione
        authService.validateAccountAccess(accountId);
        
        List<Transaction> transactions = transactionService
            .getTransactionsByAccountPaginated(accountId, 0, limit).getContent();
        
        List<TransactionData> transactionDataList = transactions.stream()
            .map(TransactionData::new)
            .collect(Collectors.toList());
        
        TransactionListResponse response = new TransactionListResponse(
            true, 
            accountId, 
            transactionDataList
        );
        
        return ResponseEntity.ok(response);
    }

    // Ottieni i dettagli di una transazione specifica
    @Operation(summary = "Ottieni dettagli transazione", description = "Recupera i dettagli di una transazione specifica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dettagli transazione recuperati"),
        @ApiResponse(responseCode = "400", description = "ID transazione non valido"),
        @ApiResponse(responseCode = "404", description = "Transazione non trovata")
    })
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long transactionId) {
        validationService.validateId(transactionId, "transazione");
        
        // Controllo autorizzazione
        authService.validateTransactionAccess(transactionId);
        
        Transaction transaction = transactionService.findById(transactionId);
        
        TransactionResponse response = new TransactionResponse(
            true,
            "Transazione trovata",
            new TransactionData(transaction)
        );
        
        return ResponseEntity.ok(response);
    }

    // Cancella una transazione sospesa
    @Operation(summary = "Cancella transazione", description = "Cancella una transazione in stato PENDING")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transazione cancellata"),
        @ApiResponse(responseCode = "400", description = "Impossibile cancellare la transazione o ID non valido"),
        @ApiResponse(responseCode = "404", description = "Transazione non trovata"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PutMapping("/{transactionId}/cancel")
    public ResponseEntity<BaseResponse> cancelTransaction(@PathVariable Long transactionId) {
        validationService.validateId(transactionId, "transazione");
        
        // Controllo autorizzazione
        authService.validateTransactionAccess(transactionId);
        
        transactionService.cancelTransaction(transactionId);
        return ResponseEntity.ok(new BaseResponse(true, "Transazione cancellata con successo"));
    }

    
    // Ottieni le transazioni dell'utente, con paginazione
    @Operation(summary = "Ottieni transazioni utente paginate", description = "Recupera le transazioni di un utente con paginazione")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transazioni utente recuperate con successo"),
        @ApiResponse(responseCode = "400", description = "Parametri di paginazione non validi"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Utente non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<PaginatedTransactionResponse> getUserTransactionsPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        validationService.validateId(userId, "utente");
        validationService.validatePagination(page, size);
        
        // Controllo autorizzazione
        authService.validateUserAccess(userId);
        
        Page<Transaction> transactionsPage = transactionService
            .getRecentTransactionsByUserPaginated(userId, page, size);
        
        List<TransactionData> transactionDataList = transactionsPage.getContent().stream()
            .map(TransactionData::new)
            .collect(Collectors.toList());
        
        PaginatedTransactionResponse response = new PaginatedTransactionResponse(
            true,
            userId,
            transactionDataList,
            page,
            transactionsPage.getTotalPages(),
            transactionsPage.getTotalElements(),
            transactionsPage.hasNext(),
            transactionsPage.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }


    // Ottieni le ultime transazioni di un utente
    @Operation(summary = "Ottieni transazioni recenti utente", description = "Recupera le ultime N transazioni di un utente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transazioni utente recuperate con successo"),
        @ApiResponse(responseCode = "400", description = "Parametro limit non valido"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Utente non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<TransactionListResponse> getRecentUserTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        validationService.validateId(userId, "utente");
        validationService.validateLimit(limit);
        
        // Controllo autorizzazione
        authService.validateUserAccess(userId);
        
        List<Transaction> transactions = transactionService
            .getRecentTransactionsByUser(userId, limit);
        
        List<TransactionData> transactionDataList = transactions.stream()
            .map(TransactionData::new)
            .collect(Collectors.toList());
        
        TransactionListResponse response = new TransactionListResponse(
            true, 
            userId, 
            transactionDataList
        );
        
        return ResponseEntity.ok(response);
    }

    // Ottieni le entrate recenti di un utente
    @Operation(summary = "Ottieni entrate recenti", description = "Calcola le entrate totali di un utente da una data specifica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Calcolo entrate completato"),
        @ApiResponse(responseCode = "400", description = "Parametri non validi"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Utente non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/user/{userId}/incomes-since")
    public ResponseEntity<AmountResponse> getUserRecentIncomes(
        @PathVariable Long userId, 
        @Parameter(description = "Data di inizio calcolo (formato ISO)", example = "2024-01-15T00:00:00")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sinceDate) {
        
        validationService.validateId(userId, "utente");
        
        // Controllo autorizzazione
        authService.validateUserAccess(userId);
        
        // Imposta data di default se non specificata
        if (sinceDate == null) {
            sinceDate = LocalDateTime.now().with(LocalTime.of(0, 0));
        }
        
        validationService.validateDate(sinceDate);
        
        BigDecimal totalAmount = transactionService.getUserRecentIncomes(userId, sinceDate);
        
        AmountResponse response = new AmountResponse(true, userId, sinceDate, totalAmount);
        response.setMessage("Calcolo entrate completato con successo");
        
        return ResponseEntity.ok(response);
    }

    // Ottieni le uscite recenti di un utente
    @Operation(summary = "Trova uscite recenti di utente", description = "Trova le uscite di un utente da una certa data, con paginazione")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Calcolo uscite completato"),
        @ApiResponse(responseCode = "400", description = "Parametri non validi (data malformata)"),
        @ApiResponse(responseCode = "403", description = "Accesso negato - non puoi accedere ai dati di altri utenti"),
        @ApiResponse(responseCode = "404", description = "Utente non trovato"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/user/{userId}/expenses-since")
    public ResponseEntity<AmountResponse> getUserRecentExpenses(
        @PathVariable Long userId, 
        @Parameter(description = "Data di inizio calcolo (formato ISO)", example = "2024-01-15T00:00:00")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sinceDate) {
        
        validationService.validateId(userId, "utente");
        
        // Controllo autorizzazione
        authService.validateUserAccess(userId);
        
        // Imposta data di default se non specificata
        if (sinceDate == null) {
            sinceDate = LocalDateTime.now().with(LocalTime.of(0, 0));
        }
        
        validationService.validateDate(sinceDate);
        
        BigDecimal totalAmount = transactionService.getUserRecentExpenses(userId, sinceDate);
        
        AmountResponse response = new AmountResponse(true, userId, sinceDate, totalAmount);
        response.setMessage("Calcolo uscite completato con successo");
        
        return ResponseEntity.ok(response);
    }
}
