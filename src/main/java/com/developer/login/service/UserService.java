package com.developer.login.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.developer.login.dto.ResponseDTO;
import com.developer.login.dto.UpdateProfileRequestDTO;
import com.developer.login.dto.UserRegisterRequestDTO;
import com.developer.login.entity.User;
import com.developer.login.enums.Role;
import com.developer.login.exception.BadRequestException;
import com.developer.login.exception.NotFoundException;
import com.developer.login.exception.UnauthorizedException;
import com.developer.login.repository.UserRepository;
import com.developer.login.utils.PasswordPolicyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PasswordPolicyService passwordPolicyService;
	private final EmailService emailService;
	private final TokenActivationService tokenActivationService;

	public ResponseDTO registerUser(UserRegisterRequestDTO request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new BadRequestException("Username já está em uso.");
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BadRequestException("E-mail já está cadastrado.");
		}

		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			throw new BadRequestException("As senhas não coincidem.");
		}

		if (!passwordPolicyService.isValidPassword(request.getNewPassword())) {
			throw new BadRequestException("A senha não atende aos requisitos.");
		}

		User user = new User();
		user.setName(request.getName());
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		user.setAge(request.getAge());
		user.setEnabled(true); //mudar para false
		user.setRole(Role.USER);

		userRepository.save(user);

		//String activationToken = tokenActivationService.generateActivationToken(user.getId().toString());
		//emailService.sendActivationEmail(user.getEmail(), activationToken);

		return new ResponseDTO("Usuário registrado com sucesso. Verifique seu e-mail para ativação.", "/auth/register");
	}

	public ResponseDTO updateUserProfile(UpdateProfileRequestDTO request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

		user.setName(request.getName());
		user.setAge(request.getAge());

		if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
			if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
				throw new BadRequestException("A senha atual deve ser informada.");
			}

			if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
				throw new UnauthorizedException("A senha atual está incorreta.");
			}

			if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
				throw new BadRequestException("As senhas não coincidem.");
			}

			if (!passwordPolicyService.isValidPassword(request.getNewPassword())) {
				throw new BadRequestException("A senha não atende aos requisitos.");
			}

			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		}

		userRepository.save(user);

		return new ResponseDTO("Perfil atualizado com sucesso.", "/auth/update-profile");
	}
}