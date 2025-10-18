package com.hrms.auth_service.services;

import com.hrms.auth_service.responce.AuthResponse;
import com.hrms.auth_service.dtos.UserDTO;
import com.hrms.auth_service.request.AuthRequest;
import com.hrms.auth_service.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;
    private final RestTemplate restTemplate;


    // URL of the UserService exposed via API Gateway
    @Value("${gateway.url}")
    private String gatewayUrl;
    @Value("${user.url}")
    private String userUrl;


    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AuthResponse authenticate(AuthRequest request) {
        String url = userUrl+"users/email/"+ request.getEmail();

        try {
            UserDTO user = restTemplate.getForObject(url, UserDTO.class);

            if (user != null && user.getPassword().equals(request.getPassword())) {
                List<String> roles = List.of("ROLE_" + user.getRole());
                String token = jwtUtil.generateToken(user.getEmail(),roles );
                AuthResponse response = new AuthResponse();
                response.setToken(token);
                return response;
            }

            throw new RuntimeException("Invalid password");
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("User not found with email: " + request.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Authentication service error: " + e.getMessage());
        }
    }



}


