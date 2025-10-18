package com.hrms.user_service.services;

import com.hrms.user_service.exception.UserServiceException;
import com.hrms.user_service.repositories.UserRepository;
import com.hrms.user_service.dtos.UserDTO;
import com.hrms.user_service.entities.User;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO register(UserDTO dto, String rawPassword) {
        User user = this.getUserByEmail(dto.getEmail());
        if(user != null){
            throw new UserServiceException("email already registered!");
        }
        user = this.convertToEntity(dto);
        user.setPassword(rawPassword);
        return convertToDTO(userRepository.save(user));
    }

    public User getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }
    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
    private User convertToEntity(UserDTO dto) {
        User  user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return user;
    }
}

