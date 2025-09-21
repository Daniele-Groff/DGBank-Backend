package com.dgbank.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;

     @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Ottieni il path della richiesta
        String requestPath = request.getServletPath();

        // Lista di path che devono essere esclusi dal filtro JWT
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Ottieni header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        // Controlla se l'header esiste e inizia con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Rimuovi "Bearer " dal token
        jwt = authHeader.substring(7);

        try {
            // Estrai la email dal token
            email = jwtService.extractEmail(jwt);

            // Se la email esiste e non c'è già autenticazione
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Valida il token
                if (jwtService.isTokenValid(jwt, email)) {
                    
                    // Crea autenticazione
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    
                    // Imposta autenticazione nel context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch(Exception e) {
            // Log dell'errore, ma continua senza autenticazione
            System.err.println("Errore durante validazione JWT: " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        // Controlla se il path è pubblico (non richiede autenticazione)
        return path.startsWith("/test") ||
               path.startsWith("/auth") ||
               path.startsWith("/swagger-ui") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/v3/api-docs.yaml") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars");
    }
}
