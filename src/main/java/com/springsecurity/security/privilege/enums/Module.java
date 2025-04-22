package com.springsecurity.security.privilege.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
@Getter
public enum Module {
    USERS(SubModule.ADMINS, SubModule.OWNERS, SubModule.SUB_ADMINS, SubModule.TENANTS),
    MANAGEMENT(SubModule.BUILDINGS, SubModule.UNITS, SubModule.LEASES,
            SubModule.TENANT_REQUESTS),
    INCOME_AND_EXPENSES(SubModule.SUPPLIERS, SubModule.EXPENSES, SubModule.ENCASEMENTS,
            SubModule.BILLINGS, SubModule.RADIATIONS, SubModule.GRATUITIES,
            SubModule.VACANTS, SubModule.DISCOUNTS, SubModule.OPEN_AP, SubModule.OPEN_AR),
    SETTINGS(SubModule.INCLUSIONS, SubModule.SERVICES, SubModule.PAYMENT_METHODS,
            SubModule.TYPE_OF_EXPENSES, SubModule.BANK_ACCOUNTS, SubModule.UNIT_TYPES);

    private final Set<SubModule> validSubModules;

    Module(SubModule... subModules) {
        this.validSubModules = new HashSet<>(Arrays.asList(subModules));
    }

    public boolean isValidSubModule(SubModule subModule) {
        return validSubModules.contains(subModule);
    }
}
