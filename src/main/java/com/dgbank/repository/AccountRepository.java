package com.dgbank.repository;

import com.dgbank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    // Trova un conto tramite il numero
    Optional<Account> findByAccountNumber(String accountNumber);
    
    // Trova i conti di un utente
    List<Account> findByOwnerId(Long ownerId);
    
    // Trova i conti attivi di un utente
    List<Account> findByOwnerIdAndIsActiveTrue(Long ownerId);

    // Controlla se il numero di un conto esiste
    boolean existsByAccountNumber(String accountNumber);

    // Query per somma totale dei saldi di un utente
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.owner.id = :userId AND a.isActive = true")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);

}
