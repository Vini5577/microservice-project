package br.com.vini.userserviceapi.service;

import br.com.vini.userserviceapi.entity.User;
import br.com.vini.userserviceapi.mapper.UserMapper;
import br.com.vini.userserviceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import models.responses.UserResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse findById(final String id) {
        return userMapper.fromEntity(userRepository.findById(id).orElse(null));
    }
}
