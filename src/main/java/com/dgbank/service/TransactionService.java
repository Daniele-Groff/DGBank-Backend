package com.dgbank.service;

import com.dgbank.exception.*;
import com.dgbank.model.Account;
import com.dgbank.model.Transaction;
import com.dgbank.model.enums.TransactionType;
import com.dgbank.model.enums.TransactionStatus;
import com.dgbank.repository.UserRepository;
import com.dgbank.repository.AccountRepository;
import com.dgbank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    // Crea un deposito (es. da ATM o bonifico esterno)
    public Transaction createDeposit(Long accountId, BigDecimal amount, String description) {

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        
        if (!account.getIsActive()) {
            throw new BusinessLogicException("Il conto non è attivo");
        }
        
        // Per depositi da sorgente esterna, in from mettiamo lo stesso conto
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.PENDING);
        
        // Crea la transazione nel db
        transaction = transactionRepository.save(transaction);
        
        // Esegui deposito
        // In mancanza di sistemi di validazione esterni,
        // il passaggio a COMPLETED è automatico
        account.deposit(amount);
        accountRepository.save(account);
        transaction.setStatus(TransactionStatus.COMPLETED);

        // Aggiorna stato in db durante return
        return transactionRepository.save(transaction);
    }

    // Crea un prelievo (es. da ATM)
    public Transaction createWithdrawal(Long accountId, BigDecimal amount, String description) {

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (!account.getIsActive()) {
            throw new BusinessLogicException("Il conto non è attivo");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessLogicException("Saldo insufficiente");
        }

        // Per transazioni a destinazione esterna, usiamo
        // lo stesso conto anche per il to
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.PENDING);
        
        // Crea transazione nel DB
        transaction = transactionRepository.save(transaction);

        // Esegui prelievo
        // In mancanza di sistemi di validazione esterni,
        // il passaggio a COMPLETED è automatico
        account.withdraw(amount);
        accountRepository.save(account);
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        return transactionRepository.save(transaction);
    }

    // Crea ed esegue un trasferimento da fondi
    public Transaction createTransfer(Long fromAccountId, String toAccountIban, 
                                     BigDecimal amount, String description) {
        String cleanIban = toAccountIban.replaceAll("\\s", "");
        Account toAccount = accountRepository.findByAccountNumber(cleanIban)
            .orElseThrow(() -> new ResourceNotFoundException("IBAN destinatario non trovato"));
        
        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account mittente", fromAccountId));

        if (fromAccountId.equals(toAccount.getId())) {
            throw new BusinessLogicException("Non puoi trasferire denaro allo stesso conto");
        }
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new BusinessLogicException("Saldo insufficiente");
        }
        
        if (!fromAccount.getIsActive() || !toAccount.getIsActive()) {
            throw new BusinessLogicException("Entrambi i conti devono essere attivi");
        }
        
        // Crea la transazione
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        
        // Salva la transazione nel db
        transaction = transactionRepository.save(transaction);
        
        // Esegui il trasferimento
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
            
        // Aggiorna i conti
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
            
        // Aggiorna lo stato della transazione automaticamente
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        return transactionRepository.save(transaction);
    }

    // Cancella una transazione sospesa
    public void cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transazione", transactionId));
        
        try {
            transaction.cancel();
            transactionRepository.save(transaction);
        } catch(Exception e) {
            throw new BusinessLogicException("Impossibile annullare: " + e.getMessage());
        }
    }
    
    // Trova una transazione per ID
    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transazione", transactionId));
    }

    // Trova le transazioni di un conto
    public List<Transaction> getTransactionsByAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account", accountId);
        }
        return transactionRepository.findByAccountId(accountId);
    }
    
    // Trova le transazioni di un utente, con paginazione
    public Page<Transaction> getRecentTransactionsByUserPaginated(Long userId, int page, int size) {
        // Verifica esistenza utente
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utente", userId);
        }
    
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return transactionRepository.findByUserIdPaginated(userId, pageable);
    }

    // Trova le ultime N transazioni di un utente
    public List<Transaction> getRecentTransactionsByUser(Long userId, int limit) {
        // Verifica esistenza utente
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utente", userId);
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by("timestamp").descending());
        Page<Transaction> page = transactionRepository.findByUserIdPaginated(userId, pageable);
        return page.getContent();
    }
    
    // Trova le transazioni in entrata di un utente, da una certa data
    public BigDecimal getUserRecentIncomes(Long userId, LocalDateTime sinceDate) {
        // Verifica esistenza utente
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utente", userId);
        }

        BigDecimal result = transactionRepository.findRecentIncomingByUserId(userId, sinceDate);
        return result != null ? result : BigDecimal.ZERO;
    }

    // Trova le transazioni in uscita di un utente, da una certa data
    public BigDecimal getUserRecentExpenses(Long userId, LocalDateTime sinceDate) {
        // Verifica esistenza utente
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utente", userId);
        }

        BigDecimal result = transactionRepository.findRecentExpensesByUserId(userId, sinceDate);
        return result != null ? result : BigDecimal.ZERO;
    }

    // Trova le transazioni di un conto con paginazione
    public Page<Transaction> getTransactionsByAccountPaginated(Long accountId, int page, int size) {
        // Verifica esistenza account
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account", accountId);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return transactionRepository.findByAccountIdPaginated(accountId, pageable);
    }

}
