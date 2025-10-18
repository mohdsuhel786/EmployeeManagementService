package com.hrms.user_service.controller;

import com.hrms.user_service.dtos.UserDTO;
import com.hrms.user_service.entities.ROLE;
import com.hrms.user_service.entities.User;
import com.hrms.user_service.services.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    @Spy
    private UserController controller;

    @Test
    void testRegisterUser() {
        Map<String, String> payload = Map.of(
                "name", "John Doe",
                "email", "john@example.com",
                "password", "secure123"
        );

        UserDTO dto = new UserDTO();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setRole(ROLE.USER);

        when(userService.register(any(UserDTO.class), eq("secure123"))).thenReturn(dto);

        ResponseEntity<UserDTO> response = controller.register(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
    }

    @Test
    void testGetAllUsers_AdminRole() {


        UserDTO user1 = new UserDTO();
        UserDTO user2 = new UserDTO();

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        ResponseEntity<?> response = controller.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<?>) response.getBody()).size());
    }




    @Test
    void testGetUserByEmail_NotFound() {
        String email = "unknown@example.com";

        when(userService.getUserByEmail(email)).thenReturn(null);

        ResponseEntity<User> response = controller.getUserByEmail(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}