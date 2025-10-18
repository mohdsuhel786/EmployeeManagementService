package com.hrms.api_gateway.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Component
public class JwtAuthFilter implements GatewayFilterFactory<JwtAuthFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public static class Config {
        // Add config fields if needed
    }

    @Override
    public GatewayFilter apply(Config config) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject();
                List<String> roles = claims.get("roles", List.class); // or "role" if that's your claim name

                if (roles == null) {
                    roles = List.of(); // fallback to empty list
                }
                String joinedRoles = String.join(",", roles);
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r -> r.headers(headers -> {
                            headers.set("X-User-Id", email);
                            headers.set("X-User-Roles", joinedRoles); // safe now
                        }))
                        .build();

                return chain.filter(mutatedExchange);

            } catch (JwtException e) {
                System.err.println("JWT validation failed: " + e.getMessage()); // 👈 helpful for debugging
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public String name() {
        return "JwtAuthFilter";
    }
}
