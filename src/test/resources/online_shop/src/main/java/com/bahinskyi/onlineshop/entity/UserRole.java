package com.bahinskyi.onlineshop.entity;

import java.util.Arrays;

public enum UserRole {
    GUEST("GUEST"),
    USER("USER"),
    ADMIN("ADMIN");

    private final String userRoleName;

    UserRole(String userRoleName) {
        this.userRoleName = userRoleName;
    }

    public String getUserRoleName() {
        return userRoleName;
    }

    public static UserRole getUserRole(String userRoleName) {

        return Arrays.stream(UserRole.values())
                .filter(userRole -> userRole.userRoleName.equalsIgnoreCase(userRoleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("UserRole name is not correct!"));
    }
}
