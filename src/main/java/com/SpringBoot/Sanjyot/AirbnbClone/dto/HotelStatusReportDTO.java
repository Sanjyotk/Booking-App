package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class HotelStatusReportDTO{
    private Long bookingCount;
    private BigDecimal totalRevenue;
    private BigDecimal avgRevenue;
    private Long totalRoomsBooked;
    private BigDecimal avgRoomsPerBooking;
}
