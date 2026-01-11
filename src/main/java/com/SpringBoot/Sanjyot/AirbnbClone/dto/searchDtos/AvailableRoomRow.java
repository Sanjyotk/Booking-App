package com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos;

import java.math.BigDecimal;
import java.time.LocalDate;

//public record AvailableRoomRow(
//        Long hotelId,
//        String hotelName,
//        String hotelCity,
//        Long roomId,
//        String roomType,
//        BigDecimal price,
//        Integer remainingRooms
//) {}

public record AvailableRoomRow(
        Long hotelId,
        String hotelName,
        String hotelCity,
        Long roomId,
        String roomType,
        BigDecimal basePrice,
        Integer remainingRooms,
        BigDecimal surgeFactor
) {}
