package com.developer.login.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	public void sendActivationEmail(String to, String token) {
		String link = "http://localhost:3000/confirmaccount?token=" + token;
		String body = generateEmailBody("activate-account-email", link);
		sendEmail(to, "Ativação de Conta", body);
	}

	public void sendPasswordResetEmail(String to, String token) {
		String link = "http://localhost:3000/resetpassword?token=" + token;
		String body = generateEmailBody("reset-password-email", link);
		sendEmail(to, "Redefinição de Senha", body);
	}

	private String generateEmailBody(String templateName, String link) {
		Context context = new Context();
		context.setVariable("link", link);
		return templateEngine.process("emails/" + templateName, context);
	}

	public void sendEmail(String to, String subject, String body) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Erro ao enviar e-mail!", e);
		}
	}
}