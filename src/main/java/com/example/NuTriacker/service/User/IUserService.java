package com.example.NuTriacker.service.User;

import com.example.NuTriacker.model.User;
import com.example.NuTriacker.request.AddUserRequest;

import java.util.List;

public interface IUserService {
    User addUser(AddUserRequest request);
    User createUser(AddUserRequest request);
    void deleteUser(String email);
    void updateUser(String password, String email);
    User getUser(String email);
    List<User> getAllUsers();
    void getDailyLog(String email, String date);
    void getAllDailyLogs(String email);
}
