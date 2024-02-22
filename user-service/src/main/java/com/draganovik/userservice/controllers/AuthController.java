package com.draganovik.userservice.controllers;

import com.draganovik.userservice.JwtService;
import com.draganovik.userservice.exceptions.ExtendedExceptions;
import com.draganovik.userservice.models.RegisterRequest;
import com.draganovik.userservice.models.RegisterResponse;
import com.draganovik.userservice.models.ValidateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/user-service")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest authenticationRequest) throws Exception {
        Optional<RegisterResponse> optionalJwtResponse = jwtService.createToken(authenticationRequest);

        if(optionalJwtResponse.isEmpty()) {
            throw new ExtendedExceptions.BadRequestException("Invalid credentials");
        }

        RegisterResponse registerResponse = optionalJwtResponse.get();
        return ResponseEntity.ok(registerResponse);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(HttpServletRequest request) throws Exception {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        String jwtToken = authorization.substring(7);
        Optional<ValidateResponse> validate = jwtService.validateToken(jwtToken);

        if (validate.isEmpty()) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        return ResponseEntity.ok(validate.get());
    }
}

