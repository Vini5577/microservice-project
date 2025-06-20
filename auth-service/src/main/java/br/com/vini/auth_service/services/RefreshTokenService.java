package br.com.vini.auth_service.services;

import br.com.vini.auth_service.models.RefreshToken;
import br.com.vini.auth_service.repositories.RefreshTokenRepository;
import br.com.vini.auth_service.security.dtos.UserDetailsDTO;
import br.com.vini.auth_service.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import models.exceptions.RefreshTokenExpired;
import models.exceptions.ResourceNotFoundException;
import models.responses.RefreshTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.expiration-sec.refresh-token}")
    private Long refreshTokenExpirationSec;

    private final RefreshTokenRepository repository;
    private final UserDetailsService userDetailsService;
    private final JWTUtils jwtUtils;

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

    public RefreshTokenResponse refreshToken(final String refreshTokenId) {
        final var refreshToken = repository.findById(refreshTokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found. Id: " + refreshTokenId));

        if(refreshToken.getExpiresAt().isBefore(now())) {
            throw new RefreshTokenExpired("Refresh token expired. Id: " + refreshTokenId);
        }


        return new RefreshTokenResponse(
                jwtUtils.generateToken((UserDetailsDTO) userDetailsService.loadUserByUsername(refreshToken.getUsername()))
        );
    }

}
