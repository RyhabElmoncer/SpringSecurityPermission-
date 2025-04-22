package com.springsecurity.security.privilege.repository;

import com.springsecurity.security.privilege.entity.Privilege;
import com.springsecurity.security.privilege.enums.PrivilegeType;
import com.springsecurity.security.privilege.enums.SubModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrivilegeRepository extends JpaRepository<Privilege, UUID> {
    List<Privilege> findByUsers_Id(UUID userId);
    Optional<Privilege> findByModuleAndSubModuleAndPrivilegeType(Module module, SubModule subModule, PrivilegeType privilegeType);
}