package com.draganovik.userservice;

import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;
import com.draganovik.userservice.models.JwtRequest;
import com.draganovik.userservice.models.JwtResponse;
import com.draganovik.userservice.models.JwtValidationResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration_time}")
    private Integer jwtExpirationTime;

    public Optional<JwtResponse> createToken(JwtRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            Date generatedDate = new Date(System.currentTimeMillis());
            Date expirationDate = new Date(generatedDate.getTime() + jwtExpirationTime);

            String jwtToken = Jwts.builder()
                    .setSubject(user.getEmail())
                    .claim("role", user.getRole().name())
                    .setIssuedAt(generatedDate)
                    .setExpiration(expirationDate)
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();

            LocalDateTime generatedLocalDate = generatedDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime expirationLocalDate = expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return Optional.of(new JwtResponse(jwtToken, user.getRole(), user.getEmail(), expirationLocalDate, generatedLocalDate));
        }

        return Optional.empty();
    }

    public Optional<JwtValidationResponse> validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken);
            if (claims.getBody().getExpiration().before(new Date())) {
                return Optional.empty();
            }
            String email = claims.getBody().get("sub").toString();
            String role = claims.getBody().get("role").toString();
            return Optional.of(new JwtValidationResponse(Role.valueOf(role), email));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
