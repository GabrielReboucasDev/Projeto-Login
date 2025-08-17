package com.developer.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDTO {
	@NotBlank(message = "O refresh token não pode estar em branco.")
	private String refreshToken;
}