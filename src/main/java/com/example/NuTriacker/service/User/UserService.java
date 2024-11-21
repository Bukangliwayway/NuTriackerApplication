package com.example.NuTriacker.service.User;

import com.example.NuTriacker.exception.UserNotFoundException;
import com.example.NuTriacker.model.User;
import com.example.NuTriacker.repository.UserRepository;
import com.example.NuTriacker.request.AddUserRequest;

import java.util.List;

public class UserService implements IUserService{

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(AddUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserNotFoundException("User with email " + request.getEmail() + " already exists");
        }

        return createUser(request);
    }

    @Override
    public User createUser(AddUserRequest request) {
        return userRepository.save(
                new User(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword())
                    );

    }

    @Override
    public void deleteUser(String email) {

    }

    @Override
    public void updateUser(String password, String email) {

    }

    @Override
    public User getUser(String email) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public void getDailyLog(String email, String date) {

    }

    @Override
    public void getAllDailyLogs(String email) {

    }
}
