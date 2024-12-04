package com.example.NuTriacker.controller;

import com.example.NuTriacker.dto.UserDto;
import com.example.NuTriacker.model.User;
import com.example.NuTriacker.request.CreateUserRequest;
import com.example.NuTriacker.request.UserUpdateRequest;
import com.example.NuTriacker.response.ApiResponse;
import com.example.NuTriacker.service.User.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;

    @GetMapping("/{userId}/user")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserDto userDto = userService.convertUserToDto(user);
        return ResponseEntity.ok(new ApiResponse("User Found!", userDto));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
        User newUser = userService.addUser(request);
        UserDto userDto = userService.convertUserToDto(newUser);
        return ResponseEntity.ok(new ApiResponse("User Added Successfully!", userDto));
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse("User Deleted Successfully!", null));
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable Long userId) {
        User updatedUser = userService.updateUser(request, userId);
        UserDto userDto = userService.convertUserToDto(updatedUser);
        return ResponseEntity.ok(new ApiResponse("User Updated Successfully!", userDto));
    }

}
