package com.dgbank.service;

import com.dgbank.model.Account;
import com.dgbank.model.Card;
import com.dgbank.exception.*;
import com.dgbank.repository.AccountRepository;
import com.dgbank.repository.CardRepository;
import com.dgbank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class CardService {
    
    @Autowired 
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public Card issueCard(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        // Validazioni business logic
        if (!account.getIsActive()) {
            throw new BusinessLogicException("Impossibile emettere carta per account congelato");
        }
        
        if (!account.getOwner().getIsActive()) {
            throw new BusinessLogicException("Impossibile emettere carta: proprietario account disattivato");
        }
        
        if (!account.getOwner().validateIdentity()) {
            throw new BusinessLogicException("Impossibile emettere carta: documento proprietario non valido");
        }
        
        Card card = new Card();
        card.setLinkedAccount(account);
        card.setOwner(account.getOwner());
        card.setCardNumber(generateCardNumber());
        card.setCvv(String.format("%03d", new Random().nextInt(1000)));
        LocalDate today = LocalDate.now();
        card.setExpiryDate(today.plusYears(4));

        return cardRepository.save(card);
    }

    // Genera un numero carta randomico
    private String generateCardNumber() {
        String cardNumberPart1 = String.format("%08d", new Random().nextInt(100000000));
        String cardNumberPart2 = String.format("%08d", new Random().nextInt(100000000));
        String cardNumber = (cardNumberPart1 + cardNumberPart2).replaceAll("(.{4})", "$1 ").trim();
        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumberPart1 = String.format("%08d", new Random().nextInt(100000000));
            cardNumberPart2 = String.format("%08d", new Random().nextInt(100000000));
            cardNumber = (cardNumberPart1 + cardNumberPart2).replaceAll("(.{4})", "$1 ").trim();
        }
        return cardNumber;
    }

    // Blocca una carta
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Carta", cardId));
        if(!card.getIsActive()) {
            throw new BusinessLogicException("Carta già bloccata");
        }
        card.block();
        cardRepository.save(card);
    }

    // Sblocca una carta
    public void activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Carta", cardId));
        if(card.getIsActive()) {
            throw new BusinessLogicException("Carta già attiva");
        }
        card.activate();
        cardRepository.save(card);
    }

    // Trova una carta per ID
    public Card findById(Long cardId) {
        return cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Carta", cardId));
    }

    // Trova una carta tramite numero
    public Card findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Carta con numero " + cardNumber.substring(cardNumber.length() - 4) + " non trovata"));
    }

    // Trova le carte di un utente
    public List<Card> findCardsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utente", userId);
        }
        return cardRepository.findByOwnerId(userId);
    }

    // Trova le carte di un conto
    public List<Card> findCardsByLinkedAccountId(Long accountId) {
        if (!userRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account", accountId);
        }
        return cardRepository.findByLinkedAccountId(accountId);
    }
}
