package com.SpringBoot.Sanjyot.AirbnbClone.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class HotelContactInfo {
    private String address;
    private String phoneNumber;
    private String email;
    private String location;
}
