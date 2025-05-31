package com.itbulls.nadine.spring.springbootdemo.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // مفتاح ثابت (حافظ عليه سريًا)
    private static final String SECRET = "supersecretkey123456789012345678901234567890";
    private final Key key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

    private final long expirationMs = 3600000; // ساعة

    public String generateToken(String username, String group, Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("group", group)
                .claim("userId", userId) // ← أضف الـ userId بالتوكن
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }


    private JwtParser getJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    public String extractUsername(String token) {
        return getJwtParser()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractGroup(String token) {
        return getJwtParser()
                .parseClaimsJws(token)
                .getBody()
                .get("group", String.class);
    }

    public boolean isTokenValid(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getJwtParser()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
    
    public Long extractUserId(String token) {
        return getJwtParser()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

}
