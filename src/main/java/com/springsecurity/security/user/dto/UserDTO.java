package com.springsecurity.security.user.dto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserDTO {
    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private boolean sendEmail;
    private String phoneNumber;
    private String role;
    private boolean enabled;
    private LocalDateTime createdDate;
    private List<String> privilegeStrings;
}
