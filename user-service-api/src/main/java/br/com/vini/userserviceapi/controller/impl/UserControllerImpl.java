package br.com.vini.userserviceapi.controller.impl;

import br.com.vini.userserviceapi.controller.UserController;
import br.com.vini.userserviceapi.entity.User;
import br.com.vini.userserviceapi.service.UserService;
import lombok.RequiredArgsConstructor;
import models.responses.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> findBId(String id) {
        return ResponseEntity.ok().body(userService.findById(id));
    }
}
