package com.developer.login.utils;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class PasswordPolicyService {

	private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

	private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

	public boolean isValidPassword(String password) {
		return pattern.matcher(password).matches();
	}
}