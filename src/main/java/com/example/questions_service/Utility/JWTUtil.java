package com.example.questions_service.Utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${refreshTokenTimeHour}")
    private static int refreshTokenTime;
    @Value("${accessTokenTimeMinute}")
    private static int accessTokenTime;
    private Key key;
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    @PostConstruct
    public void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String email) {
        logger.info("Utility: Generating access token for email: {}", email);
        var expirationTime = accessTokenTime * 60 * 1000;
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken() {
        logger.info("Utility: Generating refresh token");
        var expirationTime = refreshTokenTime * 60 * 60 * 1000;
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public TokenValidationResult validateToken(String token) throws IllegalArgumentException {
        try {
            logger.info("Utility: Validating token");
            String email = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            logger.info("Utility: Token is valid for email: {}", email);
            return new TokenValidationResult(email, false);
        } catch (ExpiredJwtException e) {
            logger.info("Utility: Token has expired but is otherwise valid");
            return new TokenValidationResult(e.getClaims().getSubject(), true);
        } catch (Exception e) {
            logger.error("Utility: Invalid token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

    public String generateNewAccessToken(String refreshToken, String email) {
        try {
            logger.info("Utility: Generating new access token using refresh token for email: {}", email);
            logger.info("Utility: Validating refresh token");
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken);
            return generateAccessToken(email);
        } catch (Exception e) {
            logger.error("Utility: Failed to generate new access token - invalid refresh token: {}", e.getMessage());
            return null;
        }
    }
}