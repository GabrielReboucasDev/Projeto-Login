package com.developer.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developer.login.dto.ResponseDTO;
import com.developer.login.dto.UpdateProfileRequestDTO;
import com.developer.login.dto.UserRegisterRequestDTO;
import com.developer.login.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO request) {
		ResponseDTO response = userService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/update-profile")
	public ResponseEntity<ResponseDTO> updateProfile(@Valid @RequestBody UpdateProfileRequestDTO request) {
		ResponseDTO response = userService.updateUserProfile(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}