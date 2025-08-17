package com.developer.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDTO {
	@Email(message = "Formato de e-mail inválido.")
	@NotBlank(message = "O e-mail não pode estar vazio.")
	@Size(max = 250, message = "O e-mail deve ter no máximo 250 caracteres.")
	private String email;
}