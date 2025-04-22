package com.springsecurity.security.privilege.service;

import com.springsecurity.security.privilege.entity.Privilege;
import com.springsecurity.security.privilege.enums.PrivilegeType;
import com.springsecurity.security.privilege.enums.SubModule;
import com.springsecurity.security.privilege.repository.PrivilegeRepository;
import com.springsecurity.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrivilegeService {
    private final PrivilegeRepository privilegeRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(PrivilegeService.class);

    public boolean hasPrivilege(Module module, SubModule subModule, PrivilegeType privilegeType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            logger.error("No authenticated user found");
            return false;
        }

        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername(); // UserDetails case
        } else if (principal instanceof String) {
            email = (String) principal; // String case (e.g., JWT authentication)
        } else {
            logger.error("Unsupported principal type: {}", principal.getClass().getName());
            return false;
        }

        // Find user ID by email
        UUID userId = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        logger.debug("User {} has privilege {}", userId, privilegeType);

        // Fetch user's privileges
        List<Privilege> privileges = privilegeRepository.findByUsers_Id(userId);
        logger.debug("Privileges {}", privileges);

        return privileges.stream().anyMatch(priv ->
                priv.getModule().equals(module) &&
                        priv.getSubModule().equals(subModule) &&
                        priv.getPrivilegeType().equals(privilegeType)
        );
    }
}
