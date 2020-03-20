package com.silva.industries.microservices.web.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import com.silva.industries.microservices.service.UserService;
import com.silva.industries.microservices.service.dto.UserDTO;
import com.silva.industries.microservices.web.api.UserAPI;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {

    private final UserService userService;

    @Override
    public UserDTO addUser(@Valid UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @Override
    public CompletableFuture<UserDTO> getUserById(@NotEmpty Long id) {
        return userService.getUserById(id);
    }

    @Override
    public CompletableFuture<List<UserDTO>> getUsers() {
        return userService.getUsers();
    }

    @Override
    public UserDTO updateUser(@Valid UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }
}
