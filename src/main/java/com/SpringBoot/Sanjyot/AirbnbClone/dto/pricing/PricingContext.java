package com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PricingContext {
    private LocalDate startDate;
    private LocalDate endDate;
    private long daysCount;
    private Integer roomsRequested;
}

