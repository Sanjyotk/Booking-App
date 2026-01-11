package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HotelSearchRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String city;
    private Integer roomsCount;

    private String roomType;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;

//    private Integer page= 0;
//    private Integer size=10;
}
