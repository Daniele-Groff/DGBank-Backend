package com.dgbank.repository;

import com.dgbank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Trova utente per email
    Optional<User> findByEmail(String email);
    
    // Controlla se l'email esiste
    boolean existsByEmail(String email);
    
    // Trova tutti gli utenti attivi
    List<User> findByIsActiveTrue();

    // Trova utente per email e password (per login)
    Optional<User> findByEmailAndIsActiveTrue(String email);

    // Query personalizzata per trovare utenti con conti
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.accounts WHERE u.id = ?1")
    Optional<User> findByIdWithAccounts(Long id);
    
}
