package com.developer.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {
	@NotBlank(message = "O username não pode estar vazio.")
	private String username;

	@NotBlank(message = "A senha não pode estar vazia.")
	private String password;
}