package br.com.vini.auth_service.models;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@Document
@Getter
public class RefreshToken {
    @Id
    private String id;
    private String username;
    private LocalDateTime createAt;
    private LocalDateTime expiresAt;
}
