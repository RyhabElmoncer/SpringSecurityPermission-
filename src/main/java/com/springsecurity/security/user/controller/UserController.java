package com.springsecurity.security.user.controller;

import com.springsecurity.security.handler.InvalidRoleException;
import com.springsecurity.security.user.dto.UserDTO;
import com.springsecurity.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/create-user")
    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'WRITE') or " +
                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'WRITE') or " +
                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'WRITE')"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String>  createUser(@RequestBody WriteRequest writeRequest
                           ) {
        UserDTO userDTO = writeRequest.getUser();
        try {
            userService.createUser(userDTO);
        } catch (InvalidRoleException e) {
            logger.error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //get methods for dropdown lists
    @GetMapping("get-list-users-name")
    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'TENANTS', 'READ') or" +
                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'READ')"
    )
    public ResponseEntity<List<GetListUsersNameResponse>> getListUsersName(){
        return userService.getListUsersName();
    }

    // update user
//    @PreAuthorize(
//            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'UPDATE') or " +
//                    "@privilegeService.hasPrivilege('USERS', 'TENANTS', 'UPDATE') or" +
//                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'UPDATE') or " +
//                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'UPDATE')"
//    )
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody WriteRequest writeRequest) {
        UserDTO userDTO = writeRequest.getUser();
        userService.updateUser( userDTO);
        return new ResponseEntity<>("User updated", HttpStatus.OK);
    }
   // Get all users
    //todo add constraint to not return the user with role admin
    @PreAuthorize("@privilegeService.hasPrivilege('USERS', 'ADMINS', 'READ')")
    @GetMapping("/get-all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return userService.getAllUsers();
    }
    // Get user by id
    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'TENANTS', 'READ') or" +
                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'READ')"
    )
    @GetMapping("get-by-id")
    public ResponseEntity<UserDTO> getUserById(@RequestParam UUID id) {
        return userService.getUserById(id);
    }
    // Get users by role
    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'TENANTS', 'READ') or" +
                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'READ')"
    )
    @GetMapping("get-by-role")
    public ResponseEntity<List<UserDTO>> getUserByRole(@RequestParam String role) {
        return userService.getUserByRole(role);
    }
    // Delete userr
    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'DELETE') or " +
                    "@privilegeService.hasPrivilege('USERS', 'TENANTS', 'DELETE') or" +
                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'DELETE') or " +
                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'DELETE')"
    )
    @DeleteMapping("/delete")
    public ResponseEntity<String>  deleteUser(@RequestParam UUID id) {
        return userService.deleteUser(id);
    }

    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'TENANTS', 'READ') or" +
                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'READ')"
    )
    @GetMapping("/count-all")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        long userCount = userService.getUserCount();
        Map<String, Long> response = new HashMap<>();
        response.put("userCount", userCount);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'READ')"
    )
    @GetMapping("/count-by-role")
    public ResponseEntity<Map<String, Object>> getUserCountByRole(@RequestParam String role) {
        long userCount = userService.getUserCountByRole(role);
        Map<String, Object> response = new HashMap<>();
        response.put("role", role);
        response.put("userCount", userCount);
        return ResponseEntity.ok(response);
    }
    // Get all users with pagination
    @PreAuthorize("@privilegeService.hasPrivilege('USERS', 'ADMINS', 'READ')")
    @GetMapping("/get-all-pageable")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userPage = userService.getAllUsers(pageable);
        return ResponseEntity.ok(userPage);
    }

    // Get users by role with pagination
    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'READ') or " +
                    "@privilegeService.hasPrivilege('USERS', 'TENANTS', 'READ') or" +
                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'READ')"
    )
    @GetMapping("get-by-role-pageable")
    public ResponseEntity<Page<UserDTO>> getUserByRole(
            @RequestParam String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userPage = userService.getUserByRole(role, pageable);
        return ResponseEntity.ok(userPage);
    }
    @PutMapping("/activate")
    @PreAuthorize(
            "@privilegeService.hasPrivilege('USERS', 'OWNERS', 'UPDATE') or " +
                    "@privilegeService.hasPrivilege('USERS', 'ADMINS', 'UPDATE') or " +
                    "@privilegeService.hasPrivilege('USERS', 'TENANTS', 'UPDATE') or" +
                    "@privilegeService.hasPrivilege('USERS', 'SUB_ADMINS', 'UPDATE')"
    )
    public ResponseEntity<String> activateOrDeactivateUser(@RequestBody ActivateRequest request) {
        userService.updateUserActivationStatus(request);
        String status = request.isEnabled() ? "activated" : "deactivated";
        return ResponseEntity.ok("User has been " + status + " successfully.");
    }



}
