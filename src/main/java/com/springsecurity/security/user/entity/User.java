package com.springsecurity.security.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springsecurity.security.audit.Auditable;
import com.springsecurity.security.privilege.entity.Privilege;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Check;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Check(constraints = "role IN ('ADMIN', 'USER')")
@Entity
@Table(name = "_user")
public class User extends Auditable implements UserDetails, Serializable {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstname;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastname;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[!@#$%^&*].*", message = "Password must contain at least one special character (!@#$%^&*)")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_privileges",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    @JsonManagedReference // Assurez-vous de ne pas avoir de boucle infinie dans la sérialisation
    private List<Privilege> privileges;

    // Les méthodes UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return privileges.stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Ajouté pour un contrôle complet de UserDetails
    }

    @Override
    public boolean isEnabled() {
        return true; // Ajouté pour activer ou désactiver l'utilisateur
    }

    public String fullName() {
        return firstname + " " + lastname;
    }


}
