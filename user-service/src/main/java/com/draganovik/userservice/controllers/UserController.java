package com.draganovik.userservice.controllers;

import com.draganovik.userservice.UserRepository;
import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;
import com.draganovik.userservice.models.UserCreatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user-service")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/users")
    public ResponseEntity<UserCreatedResponse> addUser(@RequestBody User user, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (operatorRole == Role.OWNER || (operatorRole == Role.ADMIN && user.getRole() == Role.USER)) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                User newUser = userRepository.save(user);
                UserCreatedResponse response = new UserCreatedResponse(newUser);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/users/{email}")
    public ResponseEntity<UserCreatedResponse> updateUser(@PathVariable String email, @RequestBody User user, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                if (operatorRole == Role.OWNER || (operatorRole == Role.ADMIN && existingUser.getRole() == Role.USER)) {
                    user.setEmail(email);
                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                    User updatedUser = userRepository.save(user);
                    UserCreatedResponse response = new UserCreatedResponse(updatedUser);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                if (operatorRole == Role.OWNER) {
                    userRepository.deleteByEmail(email);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            if (operatorRole == Role.OWNER || operatorRole ==  Role.ADMIN) {
                List<User> users = userRepository.findAll();
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            if (operatorRole == Role.OWNER) {
                Optional<User> user = userRepository.findByEmail(email);
                return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

