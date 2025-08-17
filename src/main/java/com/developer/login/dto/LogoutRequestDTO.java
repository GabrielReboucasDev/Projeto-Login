package com.developer.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDTO {
	@NotBlank(message = "O refresh token não pode estar vazio.")
	private String refreshToken;
}