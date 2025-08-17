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
public class TokenActivationService {

	private static final String SECRET = "activation_secret";
	private static final long EXPIRATION_TIME = 10 * 60 * 1000;

	public String generateActivationToken(String userId) {
		try {
			return JWT.create()
					.withSubject(userId)
					.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					.sign(Algorithm.HMAC256(SECRET));
		} catch (Exception e) {
			throw new BadRequestException("Erro ao gerar token de ativação");
		}
	}

	public String validateActivationToken(String token) {
		try {
			return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token).getSubject();
		} catch (JWTVerificationException e) {
			throw new BadRequestException("Token de ativação inválido ou expirado");
		}
	}
}