package br.com.vini.userserviceapi.mapper;

import br.com.vini.userserviceapi.entity.User;
import models.requests.CreateUserRequest;
import models.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE,
        nullValueCheckStrategy = ALWAYS
)
public interface UserMapper {

    UserResponse fromEntity(final User Entity);

    @Mapping(target = "id", ignore = true)
    User fromRequest(CreateUserRequest createUserRequest);
}
