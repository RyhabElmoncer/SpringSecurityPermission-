package com.springsecurity.security.user.entity;

public enum Role {
    ADMIN,
    USER;

    @Override
    public String toString() {
        // Customize the string representation if needed
        return name().toLowerCase(); // Converts the enum value to lowercase
    }
}
