package com.itbulls.nadine.spring.springbootdemo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {


	    // أنشئ المفتاح مرة واحدة فقط واحفظه في متغير
	    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	    private final long expirationMs = 3600000; // ساعة

	    public String generateToken(String username, String group) {
	        return Jwts.builder()
	            .setSubject(username)
	            .claim("group", group)
	            .setIssuedAt(new Date())
	            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
	            .signWith(key)  // لا حاجة لتحديد الـ Algorithm مرتين، لأنه محفوظ مع المفتاح
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
}
