package com.draganovik.userservice.controllers;

import com.draganovik.userservice.UserRepository;
import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;
import com.draganovik.userservice.models.UserRequest;
import com.draganovik.userservice.models.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-service/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping()
    public ResponseEntity<UserResponse> addUser(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));

            Optional<User> optionalUser = userRepository.findByEmail(userRequest.getEmail());
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (operatorRole != Role.OWNER && operatorRole != Role.ADMIN) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (operatorRole == Role.ADMIN && userRequest.getRole() != Role.USER) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (operatorRole == Role.OWNER && userRequest.getRole() == Role.OWNER) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            User newUser = new User(
                    userRequest.getEmail(),
                    bCryptPasswordEncoder.encode(userRequest.getPassword()),
                    userRequest.getRole(),
                    "UNSET"
            );

            User createdUser = userRepository.save(newUser);
            UserResponse response = new UserResponse(createdUser);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{email}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String email, @RequestBody UserRequest requestUser, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));

            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (operatorRole != Role.OWNER && operatorRole != Role.ADMIN) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (operatorRole == Role.ADMIN && requestUser.getRole() != Role.USER) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (requestUser.getRole() == Role.OWNER && existingUser.get().getRole() != Role.OWNER) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            User updateUser = existingUser.get();

            updateUser.setEmail(requestUser.getEmail());
            updateUser.setRole(requestUser.getRole());
            updateUser.setPassword(bCryptPasswordEncoder.encode(requestUser.getPassword()));

            User updatedUser = userRepository.save(updateUser);
            UserResponse response = new UserResponse(updatedUser);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));

            if (operatorRole != Role.OWNER) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            Optional<User> deleteUser = userRepository.findByEmail(email);

            if (deleteUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (deleteUser.get().getRole() == Role.OWNER) {
                return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
            }
            userRepository.deleteByEmail(email);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAllUsers(HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));

            if (operatorRole != Role.OWNER && operatorRole != Role.ADMIN) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            List<User> users = userRepository.findAll();

            return new ResponseEntity<>(users.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList()), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            if (operatorRole != Role.OWNER && operatorRole != Role.ADMIN) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            Optional<User> user = userRepository.findByEmail(email);

            return user.map(value -> new ResponseEntity<>(new UserResponse(value), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

