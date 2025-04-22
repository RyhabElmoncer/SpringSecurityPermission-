package com.springsecurity.security.user.controller;

import com.springsecurity.security.user.dto.UserDTO;
import lombok.Data;

@Data
public class WriteRequest {
    private UserDTO user;
}
