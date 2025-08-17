package com.developer.login.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
		Map<String, Object> response = new HashMap<>();
		response.put("status", ex.getStatus().value());
		response.put("error", ex.getMessage());
		response.put("timestamp", LocalDateTime.now());
		return ResponseEntity.status(ex.getStatus()).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
		Map<String, Object> response = new HashMap<>();
		response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.put("error", "Erro interno no servidor");
		response.put("timestamp", LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
	
	@ExceptionHandler(AccountNotActivatedException.class)
	public ResponseEntity<Map<String, Object>> handleAccountNotActivatedException(AccountNotActivatedException ex) {
	    Map<String, Object> response = new HashMap<>();
	    response.put("status", HttpStatus.FORBIDDEN.value());
	    response.put("error", ex.getMessage());
	    response.put("timestamp", LocalDateTime.now());
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}
}