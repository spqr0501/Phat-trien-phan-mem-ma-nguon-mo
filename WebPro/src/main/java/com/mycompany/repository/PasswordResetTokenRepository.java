package com.mycompany.repository;

import com.mycompany.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Find token by token string
    Optional<PasswordResetToken> findByToken(String token);

    // Find token by user ID
    Optional<PasswordResetToken> findByUserId(String userId);

    // Find valid token by user ID
    Optional<PasswordResetToken> findByUserIdAndUsedFalseAndExpiryDateAfter(String userId, LocalDateTime now);

    // Delete expired tokens
    void deleteByExpiryDateBefore(LocalDateTime now);

    // Delete used tokens
    void deleteByUsedTrue();
}
