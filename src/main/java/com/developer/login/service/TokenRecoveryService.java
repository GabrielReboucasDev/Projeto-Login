package com.developer.login.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.developer.login.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenRecoveryService {

	private static final String SECRET = "recovery_secret";
	private static final long EXPIRATION_TIME = 10 * 60 * 1000;

	public String generatePasswordResetToken(String userId) {
		try {
			return JWT.create()
					.withSubject(userId)
					.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					.sign(Algorithm.HMAC256(SECRET));
		} catch (Exception e) {
			throw new BadRequestException("Erro ao gerar token de recuperação de senha");
		}
	}

	public String validatePasswordResetToken(String token) {
		try {
			return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token).getSubject();
		} catch (JWTVerificationException e) {
			throw new BadRequestException("Token de recuperação inválido ou expirado");
		}
	}
}