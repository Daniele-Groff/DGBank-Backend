package com.dgbank.validation;

import com.dgbank.exception.ValidationException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ValidationService {
    
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_LIMIT = 100;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;

    // Verifica campi vuoti
    public void validateRequiredField(Object field, String fieldName) {
        if (field == null) {
            throw new ValidationException(fieldName + " è obbligatorio");
        }
        if (field instanceof String && ((String) field).trim().isEmpty()) {
            throw new ValidationException(fieldName + " non può essere vuoto");
        }
    }

    // Validazione id
    public void validateId(Long id, String resourceName) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID " + resourceName + " non valido");
        }
    }
    
    // Validazione per importi
    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("L'importo deve essere positivo");
        }
    }
    
    // Validazione per date
    public void validateDate(LocalDateTime date) {
        if (date == null) {
            throw new ValidationException("La data è obbligatoria");
        }
        
        if (date.isAfter(LocalDateTime.now())) {
            throw new ValidationException("La data non può essere nel futuro");
        }
        
        // Non accettare date troppo vecchie (es. più di 50 anni fa)
        LocalDateTime fiftyYearsAgo = LocalDateTime.now().minusYears(50);
        if (date.isBefore(fiftyYearsAgo)) {
            throw new ValidationException("Data troppo remota");
        }
    }

    // Validazione limite
    public void validateLimit(int limit) {
        if (limit <= 0) {
            throw new ValidationException("Il limite deve essere > 0");
        }
        
        if (limit > MAX_LIMIT) {
            throw new ValidationException("Il limite deve essere <= " + MAX_LIMIT);
        }
    }

    // Validazione paginazioni
    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ValidationException("Il numero di pagina deve essere >= 0");
        }
        
        if (size <= 0) {
            throw new ValidationException("La dimensione pagina deve essere > 0");
        }
        
        if (size > MAX_PAGE_SIZE) {
            throw new ValidationException("La dimensione pagina deve essere <= " + MAX_PAGE_SIZE);
        }
    }

    // Validazione email
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email è obbligatoria");
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.trim().matches(emailRegex)) {
            throw new ValidationException("Formato email non valido");
        }
        
        if (email.length() > 100) {
            throw new ValidationException("Email troppo lunga (massimo 100 caratteri)");
        }
    }

    // Validazione password
    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("La password è obbligatoria");
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("La password deve essere di almeno " + MIN_PASSWORD_LENGTH + " caratteri");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new ValidationException("Password troppo lunga (massimo " + MAX_PASSWORD_LENGTH + " caratteri)");
        }
        
        // Controlli aggiuntivi di sicurezza password
        if (password.equals(password.toLowerCase())) {
            throw new ValidationException("La password deve contenere almeno una lettera maiuscola");
        }
        
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("La password deve contenere almeno un numero");
        }
    }

    // Validazione IBAN
    public void validateIban(String iban) {
        if (iban == null || iban.trim().isEmpty()) {
            throw new ValidationException("L'IBAN è obbligatorio");
        }
        
        String cleanIban = iban.replaceAll("\\s", "");
        
        // IBAN italiano: IT + 25 caratteri
        if (!cleanIban.matches("IT\\d{2}[A-Z]\\d{5}\\d{5}\\d{12}")) {
            throw new ValidationException("Formato IBAN italiano non valido");
        }
        
        if (cleanIban.length() != 27) {
            throw new ValidationException("L'IBAN italiano deve essere di 27 caratteri");
        }
    }

    // Validazione del numero di carta
    public void validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new ValidationException("Il numero carta è obbligatorio");
        }
        
        // Rimuovi spazi per validazione
        String cleanCardNumber = cardNumber.replaceAll("\\s", "");
        
        // La maggior parte delle carte sono 16 cifre, alcune 15 (AmEx)
        if (!cleanCardNumber.matches("\\d{15,16}") && !cardNumber.contains("*")) {
            throw new ValidationException("Numero carta non valido");
        }
    }
}