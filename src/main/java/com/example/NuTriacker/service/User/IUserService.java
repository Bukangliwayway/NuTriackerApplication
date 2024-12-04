package com.example.NuTriacker.service.User;

import com.example.NuTriacker.dto.UserDto;
import com.example.NuTriacker.model.User;
import com.example.NuTriacker.request.CreateUserRequest;
import com.example.NuTriacker.request.UserUpdateRequest;

import java.util.List;

public interface IUserService {
    User addUser(CreateUserRequest request);
    User createUser(CreateUserRequest request);
    void deleteUser(Long userId);
    User updateUser(UserUpdateRequest request, Long userId);
    User getUserById(Long userId);
    UserDto convertUserToDto(User user);
    List<User> getAllUsers();
    void getDailyLog(String email, String date);
    void getAllDailyLogs(String email);
}
