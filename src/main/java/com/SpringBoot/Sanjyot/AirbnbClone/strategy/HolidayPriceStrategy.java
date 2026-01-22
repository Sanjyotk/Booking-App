package com.SpringBoot.Sanjyot.AirbnbClone.strategy;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

//@Service
@RequiredArgsConstructor
public class HolidayPriceStrategy implements PricingStrategy{

    private final PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(PricingSnapshot snapshot, PricingContext pricingContext) {
        BigDecimal price = pricingStrategy.calculatePrice(snapshot,pricingContext);
        Boolean isHolidayToday = true;
        if (isHolidayToday){
            price = price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
