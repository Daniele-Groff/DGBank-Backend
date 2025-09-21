package com.dgbank.service;

import com.dgbank.model.Account;
import com.dgbank.model.User;
import com.dgbank.exception.*;
import com.dgbank.repository.AccountRepository;
import com.dgbank.repository.UserRepository;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Account createAccount(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utente", userId));

        // Validazioni business logic
        if (!user.getIsActive()) {
            throw new BusinessLogicException("Impossibile creare account per utente disattivato");
        }

        if (!user.isAdult()) {
            throw new BusinessLogicException("L'utente deve essere maggiorenne per aprire un account");
        }

        if (!user.validateIdentity()) {
            throw new BusinessLogicException("Documento di identità non valido o scaduto");
        }

        Account account = new Account();
        account.setOwner(user);
        account.setAccountNumber(generateIBAN());

        return accountRepository.save(account);
    }

    // Genera un possibile iban italiano
    private String generateIBAN() {
        String countryCode = "IT";
        String checkDigits = String.format("%02d", new Random().nextInt(100));
        String bankCode = "X05472"; // Codice fittizio DG Bank
        String branchCode = "81110"; // Codice CAB
        String accountNumber = String.format("%012d", new Random().nextInt(999999999)); // Numero conto
        
        String iban = countryCode + checkDigits + bankCode + branchCode + accountNumber;
        
        // Verifica unicità
        while (accountRepository.existsByAccountNumber(iban)) {
            accountNumber = String.format("%012d", new Random().nextInt(999999999));
            iban = countryCode + checkDigits + bankCode + branchCode + accountNumber;
        }

        return iban;
    }

    // Trova un conto per ID
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

    // Trova un conto tramite il numero
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Account non trovato con IBAN " + accountNumber, null));
    }

    // Trova i conti di un utente
    public List<Account> findAccountsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utente", userId);
        }
        return accountRepository.findByOwnerId(userId);
    }

    // Ottieni il saldo di un conto
    public BigDecimal getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        
        return account.getBalance();
    }

    // Ottieni il saldo totale di tutti i conti di un utente
    public BigDecimal getTotalBalance(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utente", userId);
        }
        BigDecimal total = accountRepository.getTotalBalanceByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Congela un conto
    public void freezeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        if(!account.getIsActive()) {
            throw new BusinessLogicException("Conto già congelato");
        }
        account.freeze();
        accountRepository.save(account);
    }
    
    // Scongela un conto
    public void unfreezeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        if(account.getIsActive()) {
            throw new BusinessLogicException("Il conto è già attivo");
        }
        account.unfreeze();
        accountRepository.save(account);
    }
}
