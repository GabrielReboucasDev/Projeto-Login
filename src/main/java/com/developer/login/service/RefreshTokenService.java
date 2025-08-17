package com.developer.login.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.developer.login.dto.RefreshTokenRequestDTO;
import com.developer.login.dto.TokenResponseDTO;
import com.developer.login.entity.RefreshToken;
import com.developer.login.entity.User;
import com.developer.login.exception.UnauthorizedException;
import com.developer.login.jwt.JwtTokenProvider;
import com.developer.login.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public TokenResponseDTO renewAccessToken(RefreshTokenRequestDTO request) {
		RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
				.orElseThrow(() -> new UnauthorizedException("Refresh token inválido ou expirado."));

		if (jwtTokenProvider.isTokenExpired(request.getRefreshToken())) {
			refreshTokenRepository.deleteByRefreshToken(request.getRefreshToken());
			throw new UnauthorizedException("Refresh token expirado.");
		}

		User user = storedToken.getUser();

		String newAccessToken = jwtTokenProvider.generateAccessToken(user);
		String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

		storedToken.setRefreshToken(newRefreshToken);
		storedToken.setCreatedAt(LocalDateTime.now());
		storedToken.setExpiryDate(LocalDateTime.ofInstant(Instant.now().plus(3, ChronoUnit.HOURS), ZoneId.systemDefault()));

		refreshTokenRepository.save(storedToken);

		return new TokenResponseDTO(newAccessToken, newRefreshToken);
	}
}