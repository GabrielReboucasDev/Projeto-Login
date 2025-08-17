package com.developer.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {
	@NotBlank(message = "A nova senha não pode estar vazia.")
	@Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
	private String newPassword;

	@NotBlank(message = "A confirmação da nova senha não pode estar vazia.")
	@Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
	private String confirmNewPassword;
}