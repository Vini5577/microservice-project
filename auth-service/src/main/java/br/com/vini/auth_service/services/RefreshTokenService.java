package br.com.vini.auth_service.services;

import br.com.vini.auth_service.models.RefreshToken;
import br.com.vini.auth_service.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import models.responses.RefreshTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.expiration-sec.refresh-token}")
    private Long refreshTokenExpirationSec;

    private final RefreshTokenRepository repository;

    public RefreshToken save(final String username) {
        return repository.save(
                RefreshToken.builder()
                        .id(UUID.randomUUID().toString())
                        .username(username)
                        .createAt(now())
                        .expiresAt(now().plusSeconds(refreshTokenExpirationSec))
                        .build()
        );
    }

}
