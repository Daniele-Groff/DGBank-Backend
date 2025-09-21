package com.dgbank.security;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    
    // Prende la chiave da in application.properties per sicurezza, 
    // se non viene trovata utilizza quella qui presente
    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:86400000}") // 24 ore in millisecondi
    private long JWT_EXPIRATION;

    @Value("${jwt.refresh.expiration:604800000}") // 7 giorni in millisecondi
    private long REFRESH_TOKEN_EXPIRATION;

    // Genera token per un utente
    public String generateToken(String email) {
        return generateToken(new HashMap<>(), email);
    }

    // Genera il token con i claim extra
    public String generateToken(Map<String, Object> extraClaims, String email) {
        return buildToken(extraClaims, email, JWT_EXPIRATION);
    }

    // Esegue refresh del token
    public String generateRefreshToken(String email) {
        return buildToken(new HashMap<>(), email, REFRESH_TOKEN_EXPIRATION);
    }

    // Costruisce il token
    private String buildToken(Map<String, Object> extraClaims, String email, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Valida il token
    public boolean isTokenValid(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email)) && !isTokenExpired(token);
    }

    // Controlla se il token è scaduto
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Estrae data di scadenza
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // Estrae email dal token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Estrae una claim specifica dal token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Estrae le claim utilizzando la chiave
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token) // in caso di token manomesso lancia eccezione
                .getBody();
    }

        // Ottiene la chiave di firma decodificata
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
