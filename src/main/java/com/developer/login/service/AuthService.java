package com.developer.login.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.developer.login.dto.ActivateAccountRequestDTO;
import com.developer.login.dto.AuthRequestDTO;
import com.developer.login.dto.ForgotPasswordRequestDTO;
import com.developer.login.dto.LogoutRequestDTO;
import com.developer.login.dto.ResetPasswordRequestDTO;
import com.developer.login.dto.ResponseDTO;
import com.developer.login.dto.TokenResponseDTO;
import com.developer.login.entity.RefreshToken;
import com.developer.login.entity.User;
import com.developer.login.exception.AccountNotActivatedException;
import com.developer.login.exception.BadRequestException;
import com.developer.login.exception.CustomException;
import com.developer.login.exception.ForbiddenException;
import com.developer.login.exception.NotFoundException;
import com.developer.login.exception.UnauthorizedException;
import com.developer.login.jwt.JwtTokenProvider;
import com.developer.login.repository.RefreshTokenRepository;
import com.developer.login.repository.UserRepository;
import com.developer.login.utils.LoginAttemptService;
import com.developer.login.utils.PasswordPolicyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final LoginAttemptService loginAttemptService;
	private final TokenActivationService tokenActivationService;
	private final TokenRecoveryService tokenRecoveryService;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final PasswordPolicyService passwordPolicyService;

	public TokenResponseDTO authenticate(AuthRequestDTO request) {
		if (loginAttemptService.isBlocked(request.getUsername())) {
			throw new ForbiddenException("Usuário bloqueado por tentativas falhas.");
		}

		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

		if (!user.isEnabled()) {
			throw new AccountNotActivatedException("Conta não ativada. Verifique seu e-mail.");
		}

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

			loginAttemptService.loginSucceeded(request.getUsername());

			String accessToken = jwtTokenProvider.generateAccessToken(user);
			String refreshToken = jwtTokenProvider.generateRefreshToken(user);

			RefreshToken newRefreshToken = new RefreshToken();
			newRefreshToken.setRefreshToken(refreshToken);
			newRefreshToken.setExpiryDate(LocalDateTime.ofInstant(Instant.now().plus(5, ChronoUnit.MINUTES), ZoneId.systemDefault())); // Mudar para horas
			newRefreshToken.setUser(user);
			refreshTokenRepository.save(newRefreshToken);

			return new TokenResponseDTO(accessToken, refreshToken);

		} catch (BadCredentialsException e) {
			loginAttemptService.loginFailed(request.getUsername());
			throw new UnauthorizedException("Credenciais inválidas.");
		} catch (Exception e) {
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar a autenticação.");
		}
	}

	public ResponseDTO sendActivationEmail(ActivateAccountRequestDTO request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

		if (user.isEnabled()) {
			throw new BadRequestException("A conta já está ativada.");
		}

		String token = tokenActivationService.generateActivationToken(String.valueOf(user.getId()));
		emailService.sendActivationEmail(request.getEmail(), token);

		return new ResponseDTO("E-mail de ativação enviado com sucesso!", "/activate-account");
	}

	public ResponseDTO confirmAccountActivation(String token) {
		String userIdString = tokenActivationService.validateActivationToken(token);
		Integer userId = Integer.parseInt(userIdString);
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

		user.setEnabled(true);
		userRepository.save(user);

		return new ResponseDTO("Conta ativada com sucesso!", "/confirm-account");
	}

	public ResponseDTO sendPasswordResetEmail(ForgotPasswordRequestDTO request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

		String token = tokenRecoveryService.generatePasswordResetToken(user.getId().toString());
		emailService.sendPasswordResetEmail(request.getEmail(), token);

		return new ResponseDTO("E-mail de redefinição de senha enviado!", "/forgot-pasdsword");
	}

	public ResponseDTO resetPassword(String token, ResetPasswordRequestDTO request) {
		if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
			throw new BadRequestException("Senhas não coincidem.");
		}

		if (!passwordPolicyService.isValidPassword(request.getNewPassword())) {
			throw new BadRequestException("Senha não atende aos critérios de segurança.");
		}

		String userIdString = tokenRecoveryService.validatePasswordResetToken(token);
		Integer userId = Integer.parseInt(userIdString);
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);

		return new ResponseDTO("Senha redefinida com sucesso!", "/reset-password");
	}

	public void logout(LogoutRequestDTO request) {
		refreshTokenRepository.deleteByRefreshToken(request.getRefreshToken());
	}
}