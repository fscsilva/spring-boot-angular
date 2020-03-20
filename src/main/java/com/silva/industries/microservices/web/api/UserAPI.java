package com.silva.industries.microservices.web.api;

import com.silva.industries.microservices.web.ResponseBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.silva.industries.microservices.service.dto.UserDTO;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "User API", value = "This API provide operations to handle User")
public interface UserAPI {
    
    String USER_PATH = "/users";
    String USER_FIND_PATH = USER_PATH + "/{id}";

    @ApiOperation(value = "Add an User",
            notes = "All fields are required except the Id (it's self generated)",
            response = UserDTO.class,
            authorizations = {@Authorization(value = "Bearer")},
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User added"),
            @ApiResponse(code = 500, response = ResponseBody.class,
                    message = "There was an unexpected error trying to add this user"),
            @ApiResponse(code = 400, message = "The information provided is invalid", response = ResponseBody.class)})
    @PostMapping(USER_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO addUser(@RequestBody @Valid UserDTO userDTO);


    @ApiOperation(value = "Get User given Id",
            notes = "Id is required",
            response = UserDTO.class,
            authorizations = {@Authorization(value = "Bearer")},
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 500, response = ResponseBody.class,
                    message = "There was an unexpected error trying to get user"),
            @ApiResponse(code = 400, response = ResponseBody.class ,
                    message = "The information provided is invalid")})
    @GetMapping(USER_FIND_PATH)
    @ResponseStatus(HttpStatus.OK)
    CompletableFuture<UserDTO> getUserById(@PathVariable @NotEmpty Long id);

    @ApiOperation(value = "Get list of Users",
            response = List.class,
            authorizations = {@Authorization(value = "Bearer")},
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 500, response = ResponseBody.class,
                    message = "There was an unexpected error trying to get user list"),
            @ApiResponse(code = 400, response = ResponseBody.class ,
                    message = "The information provided is invalid")})
    @GetMapping(USER_PATH)
    @ResponseStatus(HttpStatus.OK)
    CompletableFuture<List<UserDTO>> getUsers();


    @ApiOperation(value = "Update User",
            notes = "All fields are required",
            response = UserDTO.class,
            authorizations = {@Authorization(value = "Bearer")},
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 500, response = ResponseBody.class,
                    message = "There was an unexpected error trying to update this user"),
            @ApiResponse(code = 400, response = ResponseBody.class ,
                    message = "The information provided is invalid")})
    @PutMapping(USER_PATH)
    @ResponseStatus(HttpStatus.OK)
    UserDTO updateUser(@RequestBody @Valid UserDTO userDTO);
}
