package com.dgbank.service;

import com.dgbank.exception.BusinessLogicException;
import com.dgbank.exception.ResourceNotFoundException;
import com.dgbank.exception.UnauthorizedException;
import com.dgbank.exception.ValidationException;
import com.dgbank.model.User;
import com.dgbank.repository.UserRepository;
import com.dgbank.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Registra un nuovo utente, restituisce utente con i dati del database
    public User registerUser(User user) {
        // Verifica che l'email non sia già registrata
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Email già registrata: " + user.getEmail());
        }
        
        // Verifica che il documento non sia già registrato
        if (documentRepository.existsByNumber(user.getDocument().getNumber())) {
            throw new ValidationException("Documento già registrato");
        }

        // Verifica che l'utente sia maggiorenne
        if (!user.isAdult()) {
            throw new BusinessLogicException("L'utente deve essere maggiorenne");
        }

        // Verifica validità documento
        if (!user.validateIdentity()) {
            throw new BusinessLogicException("Documento di identità non valido o scaduto");
        }

        // Cripta la password prima di inserirla nel db
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Trova utente per ID
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    // Trova utente per email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", null));
    }
    
    // Verifica se l'utente esiste, per email
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Verifica se l'utente esiste, per id
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    // Verifica le credenziali per il login
    public User authenticate(String email, String password) {
        // Trova l'utente per email
        User user = userRepository.findByEmailAndIsActiveTrue(email)
            .orElseThrow(() -> new UnauthorizedException("Credenziali non valide"));
        
        // Verifica password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Credenziali non valide");
        }

        return user;
    }
}
