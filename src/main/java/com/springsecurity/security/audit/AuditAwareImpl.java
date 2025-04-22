package com.springsecurity.security.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        // Retrieve the authentication object from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication exists and is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Return the principal's name as the current auditor
            return Optional.of(authentication.getName());
        }

        // Return an empty Optional if no authenticated user is present
        return Optional.empty();
    }
}
