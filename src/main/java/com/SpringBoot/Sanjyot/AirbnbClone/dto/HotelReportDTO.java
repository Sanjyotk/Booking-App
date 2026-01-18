package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class HotelReportDTO {
    private Map<BookingStatus, HotelStatusReportDTO> statusWiseReport;
}