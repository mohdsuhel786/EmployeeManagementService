package com.hrms.employee_service.feign;

import com.hrms.employee_service.dtos.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
@CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
public interface UserClient {
    @GetMapping("/api/users/id/{id}")
    UserDto getUserById(@PathVariable Long id);

    default UserDto getUserFallback(Long id, Throwable t) {
        // Fallback logic
        UserDto fallback = new UserDto();
        fallback.setId(id);
        fallback.setName("unknown");
        fallback.setEmail("unavailable");
        fallback.setRole("GUEST");
        return fallback;
    }

}
