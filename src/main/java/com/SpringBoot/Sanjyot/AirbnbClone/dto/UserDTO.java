package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
}
