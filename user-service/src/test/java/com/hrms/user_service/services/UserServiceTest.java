package com.hrms.user_service.services;
import com.hrms.user_service.dtos.UserDTO;
import com.hrms.user_service.entities.ROLE;
import com.hrms.user_service.entities.User;
import com.hrms.user_service.exception.GlobalExceptionHandler;
import com.hrms.user_service.exception.UserServiceException;
import com.hrms.user_service.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        user.setRole(ROLE.USER);
        return user;
    }
    private UserDTO sampleDTO() {
        UserDTO dto = new UserDTO();
        dto.setName("Alice");
        dto.setEmail("alice@example.com");
        dto.setRole(ROLE.USER);
        return dto;
    }


    @Test
    void testRegister() {
        UserDTO dto = new UserDTO();
        dto.setName("Alice");
        dto.setEmail("alice@example.com");
        dto.setRole(ROLE.USER);

        User savedUser = sampleUser();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.register(dto, "secret");

        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals(ROLE.USER, result.getRole());
    }

    @Test
    void testGetUserByEmail_Found() {
        User user = sampleUser();
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("alice@example.com");

        assertNotNull(result);
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        User result = userService.getUserByEmail("unknown@example.com");

        assertNull(result);
    }

    @Test
    void testGetAllUsers() {
        User user1 = sampleUser();
        User user2 = new User(1L,"Bob","bob@example.com","pass",ROLE.ADMIN);


        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
    }
    @Test
    void testRegister_Success() {
        UserDTO dto = sampleDTO();
        String password = "secret";

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = userService.register(dto, password);

        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(ROLE.USER, result.getRole());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        UserDTO dto = sampleDTO();
        String password = "secret";

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(sampleUser()));

        assertThrows(UserServiceException.class,
                () -> userService.register(dto, password));
    }

    @Test
    void testHandleValidationException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        ResponseEntity<String> response = handler.handleValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody());
    }
}