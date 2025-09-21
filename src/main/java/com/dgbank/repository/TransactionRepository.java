package com.dgbank.repository;

import com.dgbank.model.Transaction;
import com.dgbank.model.enums.TransactionType;
import com.dgbank.model.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Trova per ID della transazione
    Optional<Transaction> findByTransactionId(String transactionId);
    
    // Trova le transazioni di un conto (in entrata o uscita)
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);

    // Trova transazioni account con paginazione
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId ORDER BY t.timestamp DESC")
    Page<Transaction> findByAccountIdPaginated(@Param("accountId") Long accountId, Pageable pageable);

    // Trova transazioni utente con paginazione
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.owner.id = :userId OR t.toAccount.owner.id = :userId ORDER BY t.timestamp DESC")
    Page<Transaction> findByUserIdPaginated(@Param("userId") Long userId, Pageable pageable);

    // Trova transazioni in ingresso da data per utente
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.toAccount.owner.id = :userId AND t.type IN ('DEPOSIT', 'TRANSFER') AND t.timestamp > :sinceDate")
    BigDecimal findRecentIncomingByUserId(@Param("userId") Long userId, @Param("sinceDate") LocalDateTime sinceDate);

    // Trova transazioni in uscita da data per utente
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount.owner.id = :userId AND t.type != 'DEPOSIT' AND t.timestamp > :sinceDate")
    BigDecimal findRecentExpensesByUserId(@Param("userId") Long userId, @Param("sinceDate") LocalDateTime sinceDate);

    // Trova per stato
    List<Transaction> findByStatus(TransactionStatus status);
    
    // Trova per tipo
    List<Transaction> findByType(TransactionType type);

    // Trova transazioni in un periodo
    List<Transaction> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Trova transazioni in uscita da un conto
    List<Transaction> findByFromAccountIdOrderByTimestampDesc(Long accountId);
    
    // Trova transazioni in entrata a un conto
    List<Transaction> findByToAccountIdOrderByTimestampDesc(Long accountId);

}
