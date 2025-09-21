package com.dgbank.repository;

import com.dgbank.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>  {
    
    // Trova carta per numero
    Optional<Card> findByCardNumber(String cardNumber);
    
    // Trova le carte associate ad un conto
    List<Card> findByLinkedAccountId(Long accountId);
    
    // Trova le carte attive associate ad un conto
    List<Card> findByLinkedAccountIdAndIsActiveTrue(Long accountId);

    // Trova le carte di un utente
    List<Card> findByOwnerId(Long ownerId);

    // Trova le carte attive di un utente
    List<Card> findByOwnerIdAndIsActiveTrue(Long ownerId);

    // Controlla se un numero carta esiste
    boolean existsByCardNumber(String cardNumber);
    
    // Trova carte scadute alla data indicata
    List<Card> findByExpiryDateBefore(LocalDate date);
}
