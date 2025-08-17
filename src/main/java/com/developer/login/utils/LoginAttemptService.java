package com.developer.login.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Service
public class LoginAttemptService {

	private static final int MAX_ATTEMPTS = 5;

	private final Cache<String, Integer> attemptsCache = Caffeine.newBuilder()
			.expireAfterWrite(15, TimeUnit.MINUTES)
			.maximumSize(10000).build();

	public void loginFailed(String username) {
		attemptsCache.asMap().merge(username, 1, Integer::sum);
	}

	public void loginSucceeded(String username) {
		attemptsCache.invalidate(username);
	}

	public boolean isBlocked(String username) {
		return attemptsCache.getIfPresent(username) != null &&
				attemptsCache.getIfPresent(username) >= MAX_ATTEMPTS;
	}
}