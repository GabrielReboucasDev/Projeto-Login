package com.developer.login.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.developer.login.entity.User;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long expirationTime;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationTime;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    private JWTVerifier getVerifier() {
        return JWT.require(getAlgorithm()).build();
    }

    public String generateAccessToken(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("sub", user.getUsername())
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusMillis(expirationTime)))
                .sign(getAlgorithm());
    }

    public String generateRefreshToken(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("sub", user.getUsername())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusMillis(refreshExpirationTime)))
                .sign(getAlgorithm());
    }

    public boolean validateToken(String token) {
        try {
            getVerifier().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            logger.warn("Token inválido ou expirado: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = getVerifier().verify(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            logger.warn("Falha ao verificar expiração do token: {}", e.getMessage());
            return true;
        }
    }

    public String extractUsername(String token) {
        try {
            DecodedJWT decodedJWT = getVerifier().verify(token);
            return decodedJWT.getClaim("sub").asString();
        } catch (JWTVerificationException e) {
            logger.error("Erro ao extrair username do token: {}", e.getMessage());
            return null;
        }
    }
}