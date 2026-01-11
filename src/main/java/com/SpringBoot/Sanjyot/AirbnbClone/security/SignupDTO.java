package com.SpringBoot.Sanjyot.AirbnbClone.security;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {
    private String name;
    private String email;
    private String password;
    private Long subscription_id;

    private Set<Roles> role;
}
