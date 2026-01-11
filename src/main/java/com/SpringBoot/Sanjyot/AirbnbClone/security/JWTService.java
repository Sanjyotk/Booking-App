package com.SpringBoot.Sanjyot.AirbnbClone.security;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor("12345678901234567890123456789023".getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserEntity user){
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email",user.getEmail())
                .claim("roles",user.getRole())
                .claim("type","Access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(UserEntity user){
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("type","Refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L*60*60*24*365))
                .signWith(getSecretKey())
                .compact();
    }

    public Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public String getTokenTypeFromToken(String jwtToken) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

        return claims.get("type", String.class);
    }
}
