package com.mycompany.services;

import com.mycompany.models.PasswordResetToken;
import com.mycompany.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    // Generate and save password reset token
    public String createPasswordResetToken(String userId) {
        // Delete any existing tokens for this user
        Optional<PasswordResetToken> existing = tokenRepository.findByUserId(userId);
        existing.ifPresent(tokenRepository::delete);

        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // 24 hours validity

        PasswordResetToken resetToken = new PasswordResetToken(token, userId, expiryDate);
        tokenRepository.save(resetToken);

        return token;
    }

    // Validate token and get user ID
    public Optional<String> validateTokenAndGetUserId(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);

        if (resetToken.isEmpty()) {
            return Optional.empty();
        }

        PasswordResetToken tokenEntity = resetToken.get();

        if (!tokenEntity.isValid()) {
            return Optional.empty();
        }

        return Optional.of(tokenEntity.getUserId());
    }

    // Mark token as used
    public void markTokenAsUsed(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        resetToken.ifPresent(t -> {
            t.setUsed(true);
            tokenRepository.save(t);
        });
    }

    // Cleanup expired tokens
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    // Send reset email (MOCK - just print to console)
    public void sendPasswordResetEmail(String email, String token) {
        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        System.out.println("=".repeat(80));
        System.out.println("PASSWORD RESET EMAIL (MOCK)");
        System.out.println("=".repeat(80));
        System.out.println("To: " + email);
        System.out.println("Subject: Password Reset Request");
        System.out.println();
        System.out.println("Click the link below to reset your password:");
        System.out.println(resetLink);
        System.out.println();
        System.out.println("This link will expire in 24 hours.");
        System.out.println("If you didn't request this, please ignore this email.");
        System.out.println("=".repeat(80));
    }
}
