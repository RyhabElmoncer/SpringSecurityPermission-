package com.springsecurity.security.auth;

import com.springsecurity.security.handler.InvalidPasswordException;
import com.springsecurity.security.handler.InvalidTokenException;
import com.springsecurity.security.security.JwtService;
import com.springsecurity.security.security.TokenBlacklistService;
import com.springsecurity.security.user.entity.Role;
import com.springsecurity.security.user.entity.Token;
import com.springsecurity.security.user.entity.User;
import com.springsecurity.security.user.repository.TokenRepository;
import com.springsecurity.security.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;



@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtService jwtService;
    private String activationUrl;
    private String resetPasswordUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        validatePassword(request.getPassword()); // Validate the password
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

    }


    private String generateAndSaveActivationToken(User user) {
        //generate token
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
    //Going to be used in backoffice and front-office
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();
        claims.put("uuid", user.getId());
        claims.put("fullName", user.fullName());
        claims.put("role", user.getRole().toString());
        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(()-> new InvalidTokenException("Invalid token"));
        if (savedToken.getUser() == null) {
            throw new InvalidTokenException("User associated with the token is null");
        }
        if(LocalDateTime.now().isAfter(savedToken.getExpiredAt())){
            logger.info("Email sent to user: {}", savedToken.getUser().getEmail());
            throw new InvalidTokenException("Activation token has expired, A new token has been send to the same email");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character (!@#$%^&*)");
        }
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        var user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        //Check if the current password is correct
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw  new InvalidPasswordException("Current password is invalid");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    public void sendResetPasswordEmail(String email) throws MessagingException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
        var newToken = generateAndSaveActivationToken(user);

    }
    // Forget Password: Generate and send reset token
    public void forgetPassword(String email) throws MessagingException {

         sendResetPasswordEmail(email);
        logger.info("Password reset email sent to {}", email);
    }
    // Reset Password: Validate token and update password
    public void resetPassword(String token, String newPassword) {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            throw new InvalidTokenException("Reset token has expired");
        }

        var user = savedToken.getUser();
        if (user == null) {
            throw new InvalidTokenException("Token is not associated with any user");
        }

        // Validate and encode the new password
        validatePassword(newPassword); // Reuse password validation logic
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

        logger.info("Password successfully reset for user {}", user.getEmail());
    }

    public void signOut(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String jwt = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;

        if (jwt != null) {
            tokenBlacklistService.blacklistToken(jwt);  // Add token to the blacklist
        }
        // Invalidate session and clear SecurityContext
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
    }


}
