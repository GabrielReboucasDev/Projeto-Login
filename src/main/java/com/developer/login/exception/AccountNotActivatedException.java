package com.developer.login.exception;

import org.springframework.http.HttpStatus;

public class AccountNotActivatedException extends CustomException {
	public AccountNotActivatedException(String message) {
		super(HttpStatus.FORBIDDEN, message);
	}
}