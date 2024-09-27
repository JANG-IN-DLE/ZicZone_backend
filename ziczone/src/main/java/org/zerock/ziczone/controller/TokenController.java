package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.ziczone.dto.token.RefreshTokenRequestDTO;
import org.zerock.ziczone.security.JwtService;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        try {
            String newAccessToken = jwtService.refreshAccessToken(refreshTokenRequestDTO.getAccessToken(), refreshTokenRequestDTO.getRefreshToken());
            return ResponseEntity.ok(Map.of("access_token", newAccessToken));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Refresh Token is expired")) {
                return ResponseEntity.status(401).body("Refresh Token expired");
            } else if (e.getMessage().equals("Tokens do not match for the same user")) {
                return ResponseEntity.status(403).body("Tokens do not match for the same user");
            } else {
                return ResponseEntity.status(400).body("Invalid Token");
            }
        }
    }
}
