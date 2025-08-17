package com.developer.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.developer.login.entity.RefreshToken;

import jakarta.transaction.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);

	@Transactional
	void deleteByRefreshToken(String refreshToken);
}