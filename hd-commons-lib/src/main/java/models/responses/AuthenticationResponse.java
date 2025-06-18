package models.responses;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record AuthenticationResponse (
        String token,
        String type,
        String refreshToken
){
}
