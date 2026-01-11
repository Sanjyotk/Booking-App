package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.Gender;
import lombok.Data;

@Data
public class GuestDTO {
    private String name;
    private Gender gender;
    private Integer age;
}
