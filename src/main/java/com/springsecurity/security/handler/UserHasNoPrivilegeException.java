package com.springsecurity.security.handler;

public class UserHasNoPrivilegeException extends RuntimeException {

    public UserHasNoPrivilegeException(String msg) {
        super(msg);
    }
}
