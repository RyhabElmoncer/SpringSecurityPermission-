package com.springsecurity.security.privilege.enums;

public enum SubModule {
    // User submodules
    ADMINS,
    OWNERS,
    TENANTS,
    SUB_ADMINS,

    // Management submodules
    BUILDINGS,
    UNITS,
    LEASES,
    TENANT_REQUESTS,


    // Income and expenses submodules
    SUPPLIERS,
    EXPENSES,
    ENCASEMENTS,
    BILLINGS,
    RADIATIONS,
    GRATUITIES,
    VACANTS,
    DISCOUNTS,
    OPEN_AP,
    OPEN_AR,

    // Settings submodules
    INCLUSIONS,
    SERVICES,
    PAYMENT_METHODS,
    TYPE_OF_EXPENSES,
    BANK_ACCOUNTS,
    UNIT_TYPES
}