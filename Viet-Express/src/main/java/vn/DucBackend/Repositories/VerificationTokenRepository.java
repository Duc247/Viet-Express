package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.VerificationToken;
import vn.DucBackend.Entities.VerificationToken.TokenType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByTokenAndTokenType(String token, TokenType tokenType);

    List<VerificationToken> findByUserIdAndTokenType(Long userId, TokenType tokenType);

    List<VerificationToken> findByUserIdAndTokenTypeAndUsedAtIsNull(Long userId, TokenType tokenType);

    @Query("SELECT vt FROM VerificationToken vt WHERE vt.user.id = :userId AND vt.tokenType = :tokenType AND vt.usedAt IS NULL AND vt.expiresAt > :now")
    Optional<VerificationToken> findValidToken(@Param("userId") Long userId, @Param("tokenType") TokenType tokenType, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.user.id = :userId AND vt.tokenType = :tokenType")
    void deleteByUserIdAndTokenType(@Param("userId") Long userId, @Param("tokenType") TokenType tokenType);

    boolean existsByUserIdAndTokenTypeAndUsedAtIsNullAndExpiresAtAfter(Long userId, TokenType tokenType, LocalDateTime now);
}
