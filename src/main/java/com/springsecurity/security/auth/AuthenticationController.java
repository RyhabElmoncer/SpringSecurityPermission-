package com.springsecurity.security.auth;

import com.springsecurity.security.user.dto.UserDTO;
import com.springsecurity.security.user.entity.User;
import com.springsecurity.security.user.mapper.UserMapper;
import com.springsecurity.security.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name="Authentication")
public class AuthenticationController {
    private final AuthenticationService service;
    private final UserService userService;
    private final UserMapper userMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        System.out.println("Données reçues : " + request);
        LOGGER.info("Registering user {}", request);
        service.register(request);
        return ResponseEntity.accepted().build();
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request)
    {
        LOGGER.info("Authenticating user {}", request);
        return ResponseEntity.ok(service.authenticate(request));
    }
    @GetMapping("/activate-account")
    public ResponseEntity<String> confirm(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully.");
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request, Principal principal) {
        service.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Password changed successfully");
    }
    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody @Valid ForgetPasswordRequest request) throws MessagingException {
        service.forgetPassword(request.getEmail());
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        service.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> signOut(HttpServletRequest request) {
        try {
            service.signOut(request);
            LOGGER.info("User signed out successfully.");
            return ResponseEntity.ok("Signed out successfully.");
        } catch (Exception e) {
            LOGGER.error("Error occurred during sign-out", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during sign-out");
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            boolean emailExists = userService.doesEmailExist(email);
            response.put("emailExists", emailExists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", true);
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/currentUser")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = (User) authentication.getPrincipal();          if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        UserDTO userDTO = userMapper.toDto(user);
        return ResponseEntity.ok(userDTO);
    }



}
