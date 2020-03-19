package com.silva.industries.microservices.service;

import com.silva.industries.microservices.exception.CurrentDataNotFoundException;
import com.silva.industries.microservices.repository.UserRepository;
import com.silva.industries.microservices.repository.model.User;
import com.silva.industries.microservices.service.dto.UserDTO;
import com.silva.industries.microservices.util.ObjectMapperUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO addUser(UserDTO userDTO) {
        User user = userRepository.save(ObjectMapperUtils.map(userDTO, User.class));
        return ObjectMapperUtils.map(user, UserDTO.class);
    }

    public CompletableFuture<UserDTO> getUserById(@NotEmpty Long id) {
        return getUserModel(id)
                .thenApply(userDTO -> ObjectMapperUtils.map(userDTO, UserDTO.class));
    }

    public CompletableFuture<User> getUserModel(Long id) {
        return userRepository.findAsyncById(id)
                .thenApply(optionalUser -> optionalUser
                        .orElseThrow(() -> new CurrentDataNotFoundException("User not found with the given id")));
    }

    public CompletableFuture<List<UserDTO>> getUsers() {
        return userRepository
                .findAllByIdIsGreaterThan(0L)
                .thenApply(userList -> ObjectMapperUtils.mapAll(userList, UserDTO.class));
    }

    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.save(ObjectMapperUtils.map(userDTO, User.class));
        return ObjectMapperUtils.map(user, UserDTO.class);
    }
}
