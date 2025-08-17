package com.developer.login.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens", uniqueConstraints = { 
		@UniqueConstraint(columnNames = "refreshToken") })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String refreshToken;

	@Column(nullable = false)
	private LocalDateTime expiryDate;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}