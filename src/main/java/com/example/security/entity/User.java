package com.example.security.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Table(name = "USER")
@Entity
public @Data class User implements UserDetails {

    @Id
    private String id;
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String username;
    @Column(columnDefinition = "varchar(64)")
    private String password;
    @Column(columnDefinition = "varchar(40)")
    private String mobile;
    @Column(nullable = false, columnDefinition = "varchar(1)")
    private boolean enabled;
    @Column(columnDefinition = "text")
    private String roles;
    @Transient
    private List<GrantedAuthority> authorities;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mobile='" + mobile + '\'' +
                ", enabled=" + enabled +
                ", roles='" + roles + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
