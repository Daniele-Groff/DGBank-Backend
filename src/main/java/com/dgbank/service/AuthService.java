package com.dgbank.service;

import com.dgbank.exception.UnauthorizedException;
import com.dgbank.model.Account;
import com.dgbank.model.User;
import com.dgbank.model.Card;
import com.dgbank.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;



@Service
public class AuthService {
    
    @Autowired private UserService userService;
    @Autowired private AccountService accountService;
    @Autowired private CardService cardService;
    @Autowired private TransactionService transactionService;

    // Verifica autenticazione da ID utente
    public void validateUserAccess(Long userId) {           
        String emailFromToken = getEmailFromToken();
        if (emailFromToken == null) {
            throw new UnauthorizedException("Token non valido");
        }
        User tokenUser = userService.findByEmail(emailFromToken);
        
        if (!tokenUser.getId().equals(userId)) {
            throw new UnauthorizedException("Accesso negato: non puoi accedere ai dati di altri utenti");
        }   
    }

    // Verifica che l'utente autenticato possa vedere l'account
    public void validateAccountAccess(Long accountId) {
        String emailFromToken = getEmailFromToken();
        if (emailFromToken == null) {
            throw new UnauthorizedException("Token non valido");
        }   
        Account account = accountService.findById(accountId);
            
        if (!account.getOwner().getEmail().equals(emailFromToken)) {
            throw new UnauthorizedException("Accesso negato: non puoi accedere ai dati di altri utenti");
        }
    }

    // Verifica che l'utente loggato possa accedere alla carta
    public void validateCardAccess(Long cardId) {
        String emailFromToken = getEmailFromToken();
        if (emailFromToken == null) {
            throw new UnauthorizedException("Token non valido");
        }

        Card card = cardService.findById(cardId); // Ora lancia ResourceNotFoundException se non trova

        if (!card.getOwner().getEmail().equals(emailFromToken)) {
            throw new UnauthorizedException("Accesso negato: non puoi accedere a questa carta");
        }
    }


    // Verifica che l'utente loggato possa vedere la carta
    public void validateTransactionAccess(Long transactionId) {
        String emailFromToken = getEmailFromToken();
        if (emailFromToken == null) {
            throw new UnauthorizedException("Token non valido");
        }

        Transaction transaction = transactionService.findById(transactionId);
            
        // L'utente può accedere se possiede l'account mittente O destinatario
        if(!transaction.getFromAccount().getOwner().getEmail().equals(emailFromToken) &&
               !transaction.getToAccount().getOwner().getEmail().equals(emailFromToken)) {
            throw new UnauthorizedException("Accesso negato: non puoi accedere a queste tranzazioni");
        }
    }

    public String getEmailFromToken() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null) return null;

            return (String) auth.getPrincipal();
        } catch (Exception e) { return null; }
    }


    // Ottiene l'utente autenticato completo
    public User getAuthenticatedUserOrThrow() {
        String email = getEmailFromToken();
        if (email == null) {
            throw new UnauthorizedException("Token non valido");
        }

        return userService.findByEmail(email); // Può lanciare ResourceNotFoundException
    }


    // Verifica se esiste un'autenticazione valida
    public boolean isAuthenticated() {
        return getEmailFromToken() != null;
    }
}
