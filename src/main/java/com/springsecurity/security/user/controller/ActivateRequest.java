package com.springsecurity.security.user.controller;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Builder
@Data
public class ActivateRequest {
    private UUID userId;
    private boolean enabled;
}
