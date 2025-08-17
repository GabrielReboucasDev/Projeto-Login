package com.developer.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.developer.login.dto.ActivateAccountRequestDTO;
import com.developer.login.dto.AuthRequestDTO;
import com.developer.login.dto.ForgotPasswordRequestDTO;
import com.developer.login.dto.LogoutRequestDTO;
import com.developer.login.dto.RefreshTokenRequestDTO;
import com.developer.login.dto.ResetPasswordRequestDTO;
import com.developer.login.dto.ResponseDTO;
import com.developer.login.dto.TokenResponseDTO;
import com.developer.login.service.AuthService;
import com.developer.login.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/login")
	public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
		TokenResponseDTO tokens = authService.authenticate(request);
		return ResponseEntity.status(HttpStatus.OK).body(tokens);
	}

	@PostMapping("/activate-account")
	public ResponseEntity<ResponseDTO> activateAccount(@Valid @RequestBody ActivateAccountRequestDTO request) {
		ResponseDTO response = authService.sendActivationEmail(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/confirm-account")
	public ResponseEntity<ResponseDTO> confirmAccount(@RequestParam("token") String token) {
		ResponseDTO response = authService.confirmAccountActivation(token);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
		ResponseDTO response = authService.sendPasswordResetEmail(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping("/reset-password")
	public ResponseEntity<ResponseDTO> resetPassword(@RequestParam("token") String token,
			@Valid @RequestBody ResetPasswordRequestDTO request) {
		ResponseDTO response = authService.resetPassword(token, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<TokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
		TokenResponseDTO tokens = refreshTokenService.renewAccessToken(request);
		return ResponseEntity.status(HttpStatus.OK).body(tokens);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequestDTO request) {
		authService.logout(request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}