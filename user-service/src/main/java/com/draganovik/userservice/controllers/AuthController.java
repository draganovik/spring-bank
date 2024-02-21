package com.draganovik.userservice.controllers;

import com.draganovik.userservice.JwtService;
import com.draganovik.userservice.models.RegisterRequest;
import com.draganovik.userservice.models.RegisterResponse;
import com.draganovik.userservice.models.ValidateResponse;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest authenticationRequest) {
        Optional<RegisterResponse> optionalJwtResponse = jwtService.createToken(authenticationRequest);
        if (optionalJwtResponse.isPresent()) {
            RegisterResponse registerResponse = optionalJwtResponse.get();
            return ResponseEntity.ok(registerResponse);
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
        Optional<ValidateResponse> validate = jwtService.validateToken(jwtToken);

        if (validate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return ResponseEntity.ok(validate.get());
    }
}

