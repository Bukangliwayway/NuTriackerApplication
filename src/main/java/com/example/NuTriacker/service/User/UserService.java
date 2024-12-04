package com.example.NuTriacker.service.User;

import com.example.NuTriacker.dto.UserDto;
import com.example.NuTriacker.exception.UserNotFoundException;
import com.example.NuTriacker.model.User;
import com.example.NuTriacker.repository.UserRepository;
import com.example.NuTriacker.request.CreateUserRequest;
import com.example.NuTriacker.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;



    @Override
    public User addUser(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserNotFoundException("User with email " + request.getEmail() + " already exists");
        }

        return createUser(request);
    }

    @Override
    public User createUser(CreateUserRequest request) {
       return Optional.of(request)
               .filter(user -> !userRepository.existsByEmail(request.getEmail()))
               .map(req -> {
                   User user = new User();
                   user.setEmail(request.getEmail());
                   user.setPassword(passwordEncoder.encode(request.getPassword()));
                   user.setFirstName(request.getFirstName());
                   user.setLastName(request.getLastName());
                   return userRepository.save(user);
               }).orElseThrow(() -> new UserNotFoundException("User already exists!"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete, () -> {
            throw new UserNotFoundException("User not found!");
        });
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }


    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    @Override
    public UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
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
