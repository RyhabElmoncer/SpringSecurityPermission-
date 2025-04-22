package com.springsecurity.security.privilege.entity;

import com.springsecurity.security.privilege.enums.PrivilegeType;
import com.springsecurity.security.privilege.enums.SubModule;
import com.springsecurity.security.user.entity.User;
import com.springsecurity.security.privilege.enums.Module; // Ton enum personnalisé

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "privileges", uniqueConstraints = @UniqueConstraint(columnNames = {"module", "sub_module", "privilege_type"}))
public class Privilege implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Module module;

    @Enumerated(EnumType.STRING)
    private SubModule subModule;

    @Enumerated(EnumType.STRING)
    private PrivilegeType privilegeType;

    @ManyToMany(mappedBy = "privileges",  cascade = CascadeType.PERSIST)
    private List<User> users;

    @Override
    public String getAuthority() {
        // Return the privilege in the format MODULE:SUBMODULE:PRIVILEGE
        return module.name() + ":" + subModule.name() + ":" + privilegeType.name();
    }
}