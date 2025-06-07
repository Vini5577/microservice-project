package br.com.vini.userserviceapi.controller.impl;

import br.com.vini.userserviceapi.entity.User;
import br.com.vini.userserviceapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.requests.CreateUserRequest;
import models.requests.UpdateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static br.com.vini.userserviceapi.creator.CreatorUtils.generateMock;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerImplTest {

    public static final String BASE_URI = "/api/users";
    public static final String VALID_EMAIL = "kjadaskj@mail.com";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByIdWithSuccess() throws Exception {
        final var entity = generateMock(User.class);
        final var userId = userRepository.save(entity).getId();

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(entity.getName()))
                .andExpect(jsonPath("$.email").value(entity.getEmail()))
                .andExpect(jsonPath("$.password").value(entity.getPassword()))
                .andExpect(jsonPath("$.profiles").isArray());

        userRepository.deleteById(userId);
    }

    @Test
    void testFindByIdWithNotFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/{id}","123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Object not found. Id: 123, Type: UserResponse"))
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value("/api/users/123"))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void testFindAllWithSuccess() throws Exception {
        final var entity1 = generateMock(User.class);
        final var entity2 = generateMock(User.class);

        userRepository.saveAll(List.of(entity1, entity2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isNotEmpty())
                .andExpect(jsonPath("$[1]").isNotEmpty())
                .andExpect(jsonPath("$[0].profiles").isArray())
                .andExpect(jsonPath("$[1].profiles").isArray());

        userRepository.deleteAll(List.of(entity1, entity2));
    }

    @Test
    void testSaveUserWithSuccess() throws Exception {
        final var request = generateMock(CreateUserRequest.class).withEmail(VALID_EMAIL);

        mockMvc.perform(post(BASE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJson(request))
        ).andExpect(status().isCreated());

        userRepository.deleteByEmail(VALID_EMAIL);
    }

    @Test
    void testSaveUserWithConflict() throws Exception {
        final var entiy = generateMock(User.class).withEmail(VALID_EMAIL);
        final var request = generateMock(CreateUserRequest.class).withEmail(VALID_EMAIL);

        userRepository.save(entiy);

        mockMvc.perform(post(BASE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJson(request))
        ).andExpect(status().isConflict())
         .andExpect(jsonPath("$.message").value("Email [ " + VALID_EMAIL  + " ] already exists"))
         .andExpect(jsonPath("$.error").value(CONFLICT.getReasonPhrase()))
         .andExpect(jsonPath("$.path").value(BASE_URI))
         .andExpect(jsonPath("$.status").value(CONFLICT.value()))
         .andExpect(jsonPath("$.timestamp").isNotEmpty());

        userRepository.deleteById(entiy.getId());
    }

    @Test
    void testSaveUserWithNameEmptyThenThrowBadRequest() throws Exception {
        final var request = generateMock(CreateUserRequest.class).withName("").withEmail(VALID_EMAIL);

        mockMvc.perform(
                post(BASE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJson(request))
        ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Exception in validation attributes"))
            .andExpect(jsonPath("$.error").value("Validation Exception"))
            .andExpect(jsonPath("$.path").value(BASE_URI))
            .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.errors[?(@.fieldName == 'name' && @.message == 'Name must contain between 3 and 50 characters')]").exists())
            .andExpect(jsonPath("$.errors[?(@.fieldName == 'name' && @.message == 'Name cannot be empty')]").exists());
    }

    @Test
    void testSaveUserWithEmailEmptyThenThrowBadRequest() throws Exception {
        final var request = generateMock(CreateUserRequest.class).withEmail("");

        mockMvc.perform(
                post(BASE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJson(request))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Exception in validation attributes"))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.path").value(BASE_URI))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.fieldName == 'email' && @.message ==  'Email cannot be empty')]").exists())
                .andExpect(jsonPath("$.errors[?(@.fieldName == 'email' && @.message == 'Email must contain between 6 and 50 characters')]").exists());
    }

    @Test
    void testSaveUserWithInvalidEmailThenThrowBadRequest() throws Exception {
        final var request = generateMock(CreateUserRequest.class).withEmail("teste.");

        mockMvc.perform(
                post(BASE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJson(request))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Exception in validation attributes"))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.path").value(BASE_URI))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.fieldName == 'email' && @.message == 'Invalid email')]").exists());
    }

    @Test
    void testSaveUserWithPasswordEmptyThenThrowBadRequest() throws Exception {
        final var request = generateMock(CreateUserRequest.class).withPassword("").withEmail(VALID_EMAIL);

        mockMvc.perform(
                post(BASE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJson(request))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Exception in validation attributes"))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.path").value(BASE_URI))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.fieldName == 'password' && @.message == 'Password cannot be empty')]").exists())
                .andExpect(jsonPath("$.errors[?(@.fieldName == 'password' && @.message == 'Password must contain between 3 and 50 characters')]").exists());
    }

    @Test
    void testUpdateUserWithSuccess() throws Exception {
        final var entity = generateMock(User.class).withEmail(VALID_EMAIL);
        final var request = generateMock(UpdateUserRequest.class).withEmail(VALID_EMAIL);
        final var userId = userRepository.save(entity).getId();

        mockMvc.perform(
                    put(BASE_URI + "/{id}", userId)
                    .contentType(APPLICATION_JSON)
                    .content(toJson(request))
                )
                .andExpect(status().isOk());

        userRepository.deleteById(userId);
    }

    @Test
    void testUpdateUserWithConflict() throws Exception {
        final var entity1 = generateMock(User.class).withEmail("test@mail.com").withId("1");
        final var entity2 = generateMock(User.class).withEmail(VALID_EMAIL);
        final var request = generateMock(UpdateUserRequest.class).withEmail(VALID_EMAIL);
        final var userId = userRepository.saveAll(List.of(entity1, entity2))
                .stream()
                .filter(e -> e.getId().equals("1")).findFirst()
                .get().getId();

        mockMvc.perform(
                put(BASE_URI + "/{id}", userId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
        ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email [ " + VALID_EMAIL  + " ] already exists"))
                .andExpect(jsonPath("$.error").value(CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.path").value(BASE_URI + "/1"))
                .andExpect(jsonPath("$.status").value(CONFLICT.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        userRepository.deleteAll(List.of(entity1, entity2));
    }

    private String toJson(final Object object) throws Exception {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (final Exception e) {
            throw new Exception("Erro to convert object to json", e);
        }
    }
}