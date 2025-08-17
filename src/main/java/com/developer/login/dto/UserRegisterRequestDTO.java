package com.developer.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDTO {
	@NotBlank(message = "O nome não pode estar vazio.")
	@Size(max = 250, message = "O nome deve ter no máximo 250 caracteres.")
	private String name;

	@NotBlank(message = "O username não pode estar vazio.")
	@Size(max = 250, message = "O username deve ter no máximo 250 caracteres.")
	private String username;

	@Email(message = "Formato de e-mail inválido.")
	@NotBlank(message = "O e-mail não pode estar vazio.")
	@Size(max = 250, message = "O e-mail deve ter no máximo 250 caracteres.")
	private String email;

	@NotBlank(message = "A senha não pode estar vazia.")
	@Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
	private String newPassword;

	@NotBlank(message = "A senha não pode estar vazia.")
	@Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
	private String confirmPassword;

	@NotNull(message = "A idade é obrigatória.")
	@Min(value = 18, message = "A idade mínima permitida é 18 anos.")
	private Integer age;
}