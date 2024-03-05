package com.draganovik.userservice.controllers;

import com.draganovik.userservice.JwtService;
import com.draganovik.userservice.UserRepository;
import com.draganovik.userservice.entities.User;
import com.draganovik.userservice.exceptions.ExtendedExceptions;
import com.draganovik.userservice.models.RegisterRequest;
import com.draganovik.userservice.models.RegisterResponse;
import com.draganovik.userservice.models.UserValidationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/user-service")
public class UserAuthController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserAuthController(BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest authenticationRequest) throws Exception {

        Optional<RegisterResponse> optionalJwtResponse = jwtService.createToken(authenticationRequest);

        if (optionalJwtResponse.isEmpty()) {
            throw new ExtendedExceptions.BadRequestException("Invalid credentials");
        }

        RegisterResponse registerResponse = optionalJwtResponse.get();

        return ResponseEntity.ok(registerResponse);

    }

    @GetMapping("/validate")
    public ResponseEntity<UserValidationResponse> validate(HttpServletRequest request) throws Exception {

        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (authorization.startsWith("Basic ")) {
            String base64Credentials = authorization.substring("Basic ".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = email:password
            final String[] values = credentials.split(":", 2);

            Optional<User> oUser = userRepository.findByEmail(values[0]);

            if (oUser.isEmpty() || !bCryptPasswordEncoder.matches(values[1], oUser.get().getPassword())) {
                throw new ExtendedExceptions.UnauthorizedException();
            }

            UserValidationResponse response = new UserValidationResponse(oUser.get().getRole(), oUser.get().getEmail());

            return ResponseEntity.ok(response);
        }

        if (authorization.startsWith("Bearer")) {
            String jwtToken = authorization.substring(7);
            Optional<UserValidationResponse> validate = jwtService.validateToken(jwtToken);
            if (validate.isEmpty()) {
                throw new ExtendedExceptions.UnauthorizedException();
            }
            return ResponseEntity.ok(validate.get());
        }

        throw new ExtendedExceptions.UnauthorizedException();
    }
}

