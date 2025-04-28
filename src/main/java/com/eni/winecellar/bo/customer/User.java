package com.eni.winecellar.bo.customer;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@Entity
@Table(name="WINE_CELLAR_USER")
@Inheritance(strategy=InheritanceType.JOINED)
public class User implements UserDetails {
    @Id
    @Column(name="USERNAME", nullable=false, length=255, unique=true)
    @NonNull
    private String username;

    @Column(name="PASSWORD", nullable=false, length=68)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NonNull
    private String password;

    @Column(name="LASTNAME", nullable=false, length=90)
    @EqualsAndHashCode.Exclude
    private String lastname;

    @Column(name="FIRSTNAME", nullable=false, length=150)
    @EqualsAndHashCode.Exclude
    private String firstname;

    @Column(name="AUTHORITY", nullable=true, length=15)
    @EqualsAndHashCode.Exclude
    @NonNull
    private String authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(authority));
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
        return true;
    }
}
