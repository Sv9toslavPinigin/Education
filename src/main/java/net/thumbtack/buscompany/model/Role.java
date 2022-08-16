package net.thumbtack.buscompany.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_CLIENT("client"),
    ROLE_ADMIN("admin");

    private final String usertype;

    Role(String usertype) {
        this.usertype = usertype;
    }

    @Override
    public String getAuthority() {
        return name();
    }

    public String getUsertype() {
        return this.usertype;
    }
}