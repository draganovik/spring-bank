package com.draganovik.userservice.controllers;

import com.draganovik.userservice.UserRepository;
import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;
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

    @PostMapping("/create")
    public ResponseEntity<UserResponse> addUser(@RequestBody User user, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));

            Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if(operatorRole == Role.ADMIN && user.getRole() == Role.ADMIN) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            if (user.getRole() == Role.OWNER) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User newUser = userRepository.save(user);
            UserResponse response = new UserResponse(newUser);

            Optional<User> createdUser = userRepository.findByEmail(user.getEmail());
            if (createdUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String email, @RequestBody User user, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                if (operatorRole == Role.OWNER || (operatorRole == Role.ADMIN && existingUser.getRole() == Role.USER)) {
                    user.setEmail(email);
                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                    User updatedUser = userRepository.save(user);
                    UserResponse response = new UserResponse(updatedUser);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("delete/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email, HttpServletRequest request) {
        try {
            Optional<User> deleteUser = userRepository.findByEmail(email);

            if(deleteUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if(deleteUser.get().getRole() == Role.OWNER) {
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
            if (operatorRole == Role.OWNER || operatorRole ==  Role.ADMIN) {
                List<User> users = userRepository.findAll();

                return new ResponseEntity<>(users.stream()
                        .map(UserResponse::new)
                        .collect(Collectors.toList()), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email, HttpServletRequest request) {
        try {
            Role operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            if (operatorRole == Role.OWNER) {
                Optional<User> user = userRepository.findByEmail(email);
                return user.map(value -> new ResponseEntity<>(new UserResponse(value), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

