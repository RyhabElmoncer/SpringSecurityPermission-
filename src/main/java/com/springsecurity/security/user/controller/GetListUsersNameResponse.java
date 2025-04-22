package com.springsecurity.security.user.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@Builder
public class GetListUsersNameResponse {
    private UUID id;
    private String firstname;
    private String lastname;
    private String role;
}
