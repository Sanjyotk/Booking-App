package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.HotelEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.RoomEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InventoryDTO {
    private Long id;
    private HotelEntity hotel;
    private RoomEntity room;
    private LocalDate date;
    private Integer bookedCount;
    private Integer totalCount;
    private BigDecimal surgeFactor;
    private Boolean closed;
}


