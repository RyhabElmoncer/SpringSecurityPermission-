package com.springsecurity.security.user.service;

import com.springsecurity.security.handler.InvalidRoleException;
import com.springsecurity.security.handler.UserAlreadyExistsException;
import com.springsecurity.security.user.controller.ActivateRequest;
import com.springsecurity.security.user.controller.GetListUsersNameResponse;
import com.springsecurity.security.user.dto.UserDTO;
import com.springsecurity.security.user.entity.Role;
import com.springsecurity.security.user.entity.User;
import com.springsecurity.security.user.mapper.UserMapper;
import com.springsecurity.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public void createUser(UserDTO userDTO) throws InvalidRoleException {

        // 1. Check if the user already exists by email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists.");
        }
        // 3. Encrypt the user's password before saving
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());

        // 4. Map the userDTO to a User entity
        User user = userMapper.toEntity(userDTO);
        user.setPassword(encryptedPassword);  // Set the encrypted password

        // 6. Save the user entity
        userRepository.save(user);
    }

    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users =  userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        users.forEach(user -> userDTOs.add(userMapper.toDto(user)));
        return ResponseEntity.ok(userDTOs);

    }

    public ResponseEntity<UserDTO> getUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);

        // Log pour vérifier l'objet utilisateur
        if (user == null) {
            System.out.println("Utilisateur non trouvé pour l'ID : " + id);
        } else {
            System.out.println("Utilisateur trouvé : " + user);
        }

        UserDTO userDTO = userMapper.toDto(user);

        // Log pour vérifier le DTO
        if (userDTO == null) {
            System.out.println("Le DTO de l'utilisateur est null");
        } else {
            System.out.println("DTO de l'utilisateur : " + userDTO);
        }

        return ResponseEntity.ok(userDTO);
    }


    public ResponseEntity<List<UserDTO>> getUserByRole(String role) {
        List<User> users = userRepository.findAllByRole(Role.valueOf(role));
        List<UserDTO> userDTOs = new ArrayList<>();
        users.forEach(user -> userDTOs.add(userMapper.toDto(user)));
        return ResponseEntity.ok(userDTOs);
    }

    public ResponseEntity<String> deleteUser(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully.");
        }else{
            return ResponseEntity.ok("User not found");
        }


    }
    public void updateUser(UserDTO userDTO) {
        // Vérifier si l'utilisateur existe
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateEntityFromDto(userDTO, user);
        userRepository.save(user);
    }

    // Method to get the count of all users
    public long getUserCount() {
        return userRepository.count();
    }
    // Method to get the count of users by role
    public long getUserCountByRole(String role) {
        Role roleEnum = Role.valueOf(role);
        return userRepository.countByRole(roleEnum);
    }
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    public Page<UserDTO> getUserByRole(String role, Pageable pageable) {
        Role roleEnum = Role.valueOf(role);
        return userRepository.findByRole(roleEnum, pageable).map(userMapper::toDto);
    }

    public void updateUserActivationStatus(ActivateRequest request) {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.save(user);
    }

    public boolean doesEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
    public UserDTO getUserByEmail(String email) {
        // Recherche de l'utilisateur par son email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Retourne un DTO avec les informations pertinentes
        return UserDTO.builder()
                .id(user.getId())  // Assure-toi d'inclure l'ID de l'utilisateur si nécessaire
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole().name())  // Utilisation de 'name()' pour obtenir la valeur du rôle sous forme de String
                .enabled(user.isEnabled())
                .createdDate(user.getCreatedDate())
                .build();
    }

    public ResponseEntity<List<GetListUsersNameResponse>> getListUsersName() {
        List<User> users = userRepository.findAll();
        List<GetListUsersNameResponse> responses = users.stream().map(userMapper::toListeUsersNameResponse).collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

}
