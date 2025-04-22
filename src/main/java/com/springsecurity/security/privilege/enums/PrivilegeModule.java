package com.springsecurity.security.privilege.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum PrivilegeModule {
    USERS(SubModule.ADMINS, SubModule.SUB_ADMINS);
    private final Set<SubModule> validSubModules;

    PrivilegeModule(SubModule... subModules) {
        this.validSubModules = new HashSet<>(Arrays.asList(subModules));
    }

    public boolean isValidSubModule(SubModule subModule) {
        return validSubModules.contains(subModule);
    }
}
