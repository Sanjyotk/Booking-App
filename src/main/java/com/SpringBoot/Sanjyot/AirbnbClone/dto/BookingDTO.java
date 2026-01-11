package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.GuestEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.HotelEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.RoomEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDTO {
    private Long id;
    private HotelEntity hotel;
    private RoomEntity room;
    private UserEntity user;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus bookingStatus;
    private Set<GuestDTO> guests;
    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;

}
