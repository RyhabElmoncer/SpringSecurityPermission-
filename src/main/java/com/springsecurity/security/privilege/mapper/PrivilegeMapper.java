package com.springsecurity.security.privilege.mapper;

import com.springsecurity.security.privilege.entity.Privilege;
import com.springsecurity.security.privilege.enums.PrivilegeModule;
import com.springsecurity.security.privilege.enums.PrivilegeType;
import com.springsecurity.security.privilege.enums.SubModule;
import com.springsecurity.security.privilege.repository.PrivilegeRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrivilegeMapper {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeMapper(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    // Convert a list of privilege strings to a list of Privilege entities
    public List<Privilege> toEntities(List<String> privilegeStrings) {
        if (privilegeStrings == null || privilegeStrings.isEmpty()) {
            return Collections.emptyList();
        }

        return privilegeStrings.stream()
                .map(privilegeString -> {
                    String[] parts = privilegeString.split(":");
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Invalid privilege format: " + privilegeString);
                    }

                    PrivilegeModule module = PrivilegeModule.valueOf(parts[0]);
                    SubModule subModule = SubModule.valueOf(parts[1]);
                    PrivilegeType privilegeType = PrivilegeType.valueOf(parts[2]);

                    if (!module.isValidSubModule(subModule)) {
                        throw new IllegalArgumentException("Invalid submodule for module: " + privilegeString);
                    }

                    return privilegeRepository.findByModuleAndSubModuleAndPrivilegeType(module, subModule, privilegeType)
                            .orElseThrow(() -> new RuntimeException("Privilege not found: " + privilegeString));
                })
                .collect(Collectors.toList());
    }

    // Convert a Privilege entity to a string representation
    public String toString(Privilege privilege) {
        return privilege.getModule().name() + ":" +
                privilege.getSubModule().name() + ":" +
                privilege.getPrivilegeType().name();
    }
}
