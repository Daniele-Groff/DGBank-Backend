package com.dgbank.controller;

import com.dgbank.model.User;
import com.dgbank.model.Document;
import com.dgbank.dto.*;
import com.dgbank.model.Address;
import com.dgbank.service.UserService;
import com.dgbank.security.JwtService;
import com.dgbank.exception.UnauthorizedException;
import com.dgbank.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "Endpoint per autenticazione e gestione utenti")
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ValidationService validationService;

    @Operation(summary = "Registra nuovo utente", description = "Crea un nuovo account utente nel sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Utente registrato con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi o utente già esistente"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Validazione dati con lancio eccezioni
        validationService.validateEmail(request.getEmail());
        validationService.validatePassword(request.getPassword());
        validationService.validateRequiredField(request.getFirstName(), "Nome");
        validationService.validateRequiredField(request.getLastName(), "Cognome");
        validationService.validateRequiredField(request.getDateOfBirth(), "Data di nascita");
        validationService.validateRequiredField(request.getPhoneNumber(), "Numero di telefono");
        validationService.validateRequiredField(request.getDocumentType(), "Tipo documento");
        validationService.validateRequiredField(request.getDocumentNumber(), "Numero documento");

        // Crea utente
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setDateOfBirth(request.getDateOfBirth());
        newUser.setPhoneNumber(request.getPhoneNumber());
 
        // Crea documento
        Document document = new Document();
        document.setType(request.getDocumentType());
        document.setNumber(request.getDocumentNumber());
        document.setExpiryDate(request.getDocumentExpiry());
        document.setIssuer(request.getDocumentIssuer());
        newUser.setDocument(document);
        
        // Crea indirizzo
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        newUser.setAddress(address);

        User savedUser = userService.registerUser(newUser);

        // Genera JWT token per auto-login dopo registrazione
        String jwtToken = jwtService.generateToken(savedUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());

        AuthResponse response = new AuthResponse(
            true,
            "Registrazione completata con successo",
            jwtToken,
            refreshToken,
            new UserData(savedUser)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint login
    @Operation(summary = "Effettua login", description = "Effettua il login di un utente esistente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login effettuato con successo"),
        @ApiResponse(responseCode = "400", description = "Dati mancanti o non validi"),
        @ApiResponse(responseCode = "401", description = "Credenziali non valide"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        // Validazioni
        validationService.validateEmail(loginRequest.getEmail());
        validationService.validateRequiredField(loginRequest.getPassword(), "Password");

        User user = userService.authenticate(
            loginRequest.getEmail().trim().toLowerCase(), 
            loginRequest.getPassword()
        ); // Può lanciare UnauthorizedException

        String jwtToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        AuthResponse response = new AuthResponse(
            true,
            "Login effettuato con successo",
            jwtToken,
            refreshToken,
            new UserData(user)
        );

        return ResponseEntity.ok(response);
    }

    // Refresh del token JWT
    @Operation(summary = "Aggiorna token", description = "Ottiene un nuovo token usando il refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshato con successo"),
        @ApiResponse(responseCode = "400", description = "Refresh token mancante"),
        @ApiResponse(responseCode = "401", description = "Refresh token non valido o scaduto"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        // Validazione parametro
        validationService.validateRequiredField(request.getRefreshToken(), "Refresh token");
  
        String refreshToken = request.getRefreshToken();
        String email = jwtService.extractEmail(refreshToken);
            
        if (email == null || !jwtService.isTokenValid(refreshToken, email)) {
            throw new UnauthorizedException("Refresh token non valido");
        }

        String newAccessToken = jwtService.generateToken(email);
                
        RefreshTokenResponse response = new RefreshTokenResponse(
            true,
            "Token refreshato con successo",
            newAccessToken,
            refreshToken
        );
    
        return ResponseEntity.ok(response);
    }

    // Valida il token JWT
    @Operation(summary = "Convalida token", description = "Convalida il token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Validazione completata (controlla il campo 'valid' nella response)"),
        @ApiResponse(responseCode = "400", description = "Token mancante"),
        @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/validate-token")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody ValidateTokenRequest request) {
        validationService.validateRequiredField(request.getToken(), "Token");
        
        String token = request.getToken();
        String email = jwtService.extractEmail(token);
        boolean isValid = jwtService.isTokenValid(token, email);
        
        TokenValidationResponse response = new TokenValidationResponse(
            isValid,
            (isValid ? email : null)
        );
        
        return ResponseEntity.ok(response);
    }
}
