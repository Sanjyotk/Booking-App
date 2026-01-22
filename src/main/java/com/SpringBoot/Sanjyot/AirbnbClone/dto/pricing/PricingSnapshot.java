package com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing;

import java.math.BigDecimal;

public record
PricingSnapshot(
        BigDecimal basePrice,
        BigDecimal surgeFactor,
        Integer remainingRooms,
        Long daysCount
) {}
