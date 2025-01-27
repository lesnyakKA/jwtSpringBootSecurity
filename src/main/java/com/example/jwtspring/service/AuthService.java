package com.example.jwtspring.service;

import com.example.jwtspring.dto.AuthRequest;
import com.example.jwtspring.dto.AuthResponse;
import com.example.jwtspring.dto.RefreshTokenRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        String username = authentication.getName();
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (jwtUtil.validateToken(refreshToken)) {
            Claims claims = jwtUtil.parseToken(refreshToken);
            String username = claims.getSubject();
            String accessToken = jwtUtil.generateAccessToken(username);
            return new AuthResponse(accessToken, refreshToken);
        }
        throw new RuntimeException("Invalid refresh token");
    }
}