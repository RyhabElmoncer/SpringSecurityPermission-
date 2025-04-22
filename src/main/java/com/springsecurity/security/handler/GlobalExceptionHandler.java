package com.springsecurity.security.handler;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.springsecurity.security.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleLockedException(LockedException exp){

        return ResponseEntity
                .status(ACCOUNT_LOCKED.getHttpStatus()
                        )
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getCode())
                                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleMessagingException(MessagingException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    //var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }
    @ExceptionHandler(UserHasNoPrivilegeException.class)
    public ResponseEntity<ExceptionResponse> UserHasNoPrivilegeException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    //var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(HAS_NO_PRIVILEGE.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        //log the exception
        logger.error(exp.getMessage(), exp);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal error, please contact the admin")
                                .error(exp.getMessage())
                                .build()
                );
    }

    // Handle IllegalArgumentException (for null DTOs)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                                .businessErrorDescription("Invalid input provided")
                                .error(ex.getMessage())
                                .build()
                );
    }
    // Handle UserAlreadyExistsException (for duplicate email)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.error("User already exists error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCodes.BAD_CREDENTIALS.getCode())
                                .businessErrorDescription("User with this email already exists")
                                .error(ex.getMessage())
                                .build()
                );
    }
    // Handle InvalidRoleException (for role validation)
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRoleException(InvalidRoleException ex) {
        logger.error("Role is invalid error: {}", ex.getMessage());
        return ResponseEntity
                .status(INVALID_ROLE.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(INVALID_ROLE.getCode())
                                .businessErrorDescription("The role is invalid")
                                .error(ex.getMessage())
                                .build()
                );
    }
    // Handle InvalidPasswordException (for password validation)
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        logger.error("Password is invalid error: {}", ex.getMessage());
        return ResponseEntity
                .status(INVALID_PASSWORD.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCodes.INVALID_PASSWORD.getCode())
                                .businessErrorDescription("The password is invalid")
                                .error(ex.getMessage())
                                .build()
                );
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        logger.error("Access Denied: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("You do not have permission to perform this action.", HttpStatus.FORBIDDEN);
    }
}
