package com.albymens.note_app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${security.jwt.secret.key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private Long expiration;

    public String generateToken(String username, Long userId){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String extractUsername(String token){
        return extractClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            Claims claims = extractClaims(token);
            Date expiryDate = claims.getExpiration();
            return username.equals(userDetails.getUsername()) && expiryDate.after(new Date());
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

}