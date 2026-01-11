package com.SpringBoot.Sanjyot.AirbnbClone.entities;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Roles> role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream()
                .map(roles -> new SimpleGrantedAuthority("ROLE_"+roles.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
