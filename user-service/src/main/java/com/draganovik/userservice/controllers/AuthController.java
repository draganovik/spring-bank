package com.draganovik.userservice.controllers;

import com.draganovik.userservice.JwtService;
import com.draganovik.userservice.models.JwtRequest;
import com.draganovik.userservice.models.JwtResponse;
import com.draganovik.userservice.models.JwtValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/user-service")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody JwtRequest authenticationRequest) {
        Optional<JwtResponse> optionalJwtResponse = jwtService.createToken(authenticationRequest);
        if (optionalJwtResponse.isPresent()) {
            JwtResponse jwtResponse = optionalJwtResponse.get();
            return ResponseEntity.ok(jwtResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String jwtToken = authorization.substring(7);
        Optional<JwtValidationResponse> validate = jwtService.validateToken(jwtToken);

        if (validate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return ResponseEntity.ok(validate.get());
    }
}

