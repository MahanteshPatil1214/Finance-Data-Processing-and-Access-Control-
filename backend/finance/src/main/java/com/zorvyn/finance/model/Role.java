package com.zorvyn.finance.model;

/**
 * Defines the authorization levels within the Zorvyn platform.
 * <p>
 * These roles are used in conjunction with Spring Security's {@code @PreAuthorize}
 * annotations to restrict access to specific administrative and analytical endpoints.
 * </p>
 */
public enum Role {

    /**
     * Full system access. 
     * Can manage users, create/edit/delete all financial records, 
     * and modify the system-wide category registry.
     */
    ADMIN,

    /**
     * Specialized data access. 
     * Can view global platform statistics, perform trend analysis, 
     * and monitor financial anomalies, but cannot modify administrative settings.
     */
    ANALYST,

    /**
     * Standard user access. 
     * Limited to viewing their own dashboard, personal financial records, 
     * and managing their own budgets.
     */
    VIEWER
}