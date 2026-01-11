package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.HotelContactInfo;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import lombok.Data;

@Data
public class HotelDTO {
    private Long id;
    private UserEntity owner;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}
