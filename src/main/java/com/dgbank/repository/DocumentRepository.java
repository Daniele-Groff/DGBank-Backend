package com.dgbank.repository;

import com.dgbank.model.Document;
import com.dgbank.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    // Trova documento tramite il numero
    Optional<Document> findByNumber(String number);
    
    // Controlla se il numero documento esiste
    boolean existsByNumber(String number);
    
    // Trova documenti per tipo
    List<Document> findByType(DocumentType type);
    
    // Trova documenti scaduti alla data indicata
    List<Document> findByExpiryDateBefore(LocalDate date);

}
