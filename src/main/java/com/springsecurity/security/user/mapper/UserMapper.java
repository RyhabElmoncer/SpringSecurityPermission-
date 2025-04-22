package com.springsecurity.security.user.mapper;
import com.springsecurity.security.privilege.entity.Privilege;
import com.springsecurity.security.privilege.mapper.PrivilegeMapper;
import com.springsecurity.security.user.controller.GetListUsersNameResponse;
import com.springsecurity.security.user.dto.UserDTO;
import com.springsecurity.security.user.entity.Role;
import com.springsecurity.security.user.entity.User;
import com.springsecurity.security.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;



@Component

public class UserMapper {

    private final PrivilegeMapper privilegeMapper;
    private final UserRepository userRepository;

    public UserMapper(PrivilegeMapper privilegeMapper, UserRepository userRepository ) {
        this.privilegeMapper = privilegeMapper;
        this.userRepository = userRepository;
    }

    public User toEntity(UserDTO userDTO) {
        if(userDTO == null) {
            return null;
        }
        User user = (userDTO.getId() != null) ? userRepository.findById(userDTO.getId()).orElse(new User())
                : new User();

        // Convert privileges from the userDTO (List<String>) to List<Privilege> entities
        List<Privilege> privileges = privilegeMapper.toEntities(userDTO.getPrivilegeStrings());
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(Role.valueOf(userDTO.getRole().toUpperCase()));
        user.setPrivileges(privileges);
        // le mapping de l'adresse

        // Map the user dto to entity
        return user;
    }


    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        // Convert privileges from List<Privilege> to List<String> representation
        List<String> privilegeStrings = user.getPrivileges().stream()
                .map(privilegeMapper::toString)
                .toList();

        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())

                .role(user.getRole().name())
                .privilegeStrings(privilegeStrings)
                .build();
    }
    public void updateEntityFromDto(UserDTO userDTO, User user) {
        List<Privilege> privileges = privilegeMapper.toEntities(userDTO.getPrivilegeStrings());
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setRole(Role.valueOf(userDTO.getRole().toUpperCase()));
        user.setPrivileges(privileges);
    }
    public GetListUsersNameResponse toListeUsersNameResponse (User user) {
        return GetListUsersNameResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
