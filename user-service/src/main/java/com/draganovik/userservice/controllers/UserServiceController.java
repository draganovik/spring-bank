package com.draganovik.userservice.controllers;

import com.draganovik.userservice.UserRepository;
import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;
import com.draganovik.userservice.exceptions.ErrorResponse;
import com.draganovik.userservice.exceptions.ExtendedExceptions;
import com.draganovik.userservice.feign.FeignBankAccount;
import com.draganovik.userservice.feign.FeignCryptoWallet;
import com.draganovik.userservice.models.UserRequest;
import com.draganovik.userservice.models.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-service")
public class UserServiceController {

    private final UserRepository userRepository;
    private final FeignBankAccount feignBankAccount;
    private final FeignCryptoWallet feignCryptoWallet;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceController(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository,
                                 FeignBankAccount feignBankAccount, FeignCryptoWallet feignCryptoWallet) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.feignBankAccount = feignBankAccount;
        this.feignCryptoWallet = feignCryptoWallet;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest, HttpServletRequest request) throws Exception {
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.OWNER && operatorRole != Role.ADMIN) {
            throw new ExtendedExceptions.ForbiddenException("Only OWNER and ADMIN can perform this operation.");
        }

        if (operatorRole == Role.ADMIN && userRequest.getRole() != Role.USER) {
            throw new ExtendedExceptions.ForbiddenException("ADMIN can only create new USERs.");
        }

        if (operatorRole == Role.OWNER && userRequest.getRole() == Role.OWNER) {
            throw new ExtendedExceptions.ForbiddenException("OWNER already exist in the system.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(userRequest.getEmail());
        if (optionalUser.isPresent()) {
            throw new ExtendedExceptions.BadRequestException("Can't create profile with given email address.");
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
    }

    @PutMapping("/users/{email}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable @Email @Valid String email, @RequestBody @Valid UserRequest requestUser, HttpServletRequest request) throws Exception {
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.OWNER && operatorRole != Role.ADMIN) {
            throw new ExtendedExceptions.ForbiddenException("Only OWNER and ADMIN can perform this operation.");
        }

        if (operatorRole == Role.ADMIN && requestUser.getRole() != Role.USER) {
            throw new ExtendedExceptions.ForbiddenException("ADMIN can only update USER profiles.");
        }

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("Can't find requested user profile.");
        }

        if (requestUser.getRole() == Role.OWNER && existingUser.get().getRole() != Role.OWNER) {
            throw new ExtendedExceptions.ForbiddenException("Promoting to OWNER is not supported.");
        }

        User updateUser = existingUser.get();

        if (!Objects.equals(updateUser.getEmail(), requestUser.getEmail())) {
            try {
                feignBankAccount.updateBankAccountEmail(
                        existingUser.get().getEmail(),
                        requestUser.getEmail(),
                        operatorRole.name());

                feignCryptoWallet.updateCryptoWalletEmail(
                        existingUser.get().getEmail(),
                        requestUser.getEmail(),
                        operatorRole.name());
            } catch (Exception ignore) {
            }

            updateUser.setEmail(requestUser.getEmail());
        }

        updateUser.setRole(requestUser.getRole());
        updateUser.setPassword(bCryptPasswordEncoder.encode(requestUser.getPassword()));

        User updatedUser = userRepository.save(updateUser);
        UserResponse response = new UserResponse(updatedUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<?> deleteUserByEmail(@PathVariable @Email @Valid String email, HttpServletRequest request) throws Exception {
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.OWNER) {
            throw new ExtendedExceptions.ForbiddenException("Only OWNER can perform this operation.");
        }

        Optional<User> deleteUser = userRepository.findByEmail(email);

        if (deleteUser.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("Can't find requested user profile.");
        }

        if (deleteUser.get().getRole() == Role.OWNER) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.I_AM_A_TEAPOT.value(),
                    "The service refuses to brew coffee because it is, permanently, a teapot.",
                    ZonedDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.I_AM_A_TEAPOT);
        }

        userRepository.deleteByEmail(email);

        if (deleteUser.get().getRole() == Role.USER) {
            try {
                feignBankAccount.deleteBankAccount(email, operatorRole.name());
                feignCryptoWallet.deleteCryptoWallet(email, operatorRole.name());
            } catch (Exception ignored) {
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(HttpServletRequest request) throws Exception {
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.OWNER && operatorRole != Role.ADMIN) {
            throw new ExtendedExceptions.ForbiddenException("Only OWNER and ADMIN can perform this operation.");
        }

        List<User> users = userRepository.findAll();

        return new ResponseEntity<>(users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable @Email @Valid String email, HttpServletRequest request) throws Exception {
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.OWNER && operatorRole != Role.ADMIN) {
            throw new ExtendedExceptions.ForbiddenException("Only OWNER and ADMIN can perform this operation.");
        }

        Optional<User> user = userRepository.findByEmail(email);

        return user.map(value -> new ResponseEntity<>(new UserResponse(value), HttpStatus.OK))
                .orElseThrow(() -> new ExtendedExceptions.NotFoundException("Can't find requested user profile."));
    }
}

