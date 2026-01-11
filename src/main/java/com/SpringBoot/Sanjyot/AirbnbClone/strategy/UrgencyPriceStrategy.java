package com.SpringBoot.Sanjyot.AirbnbClone.strategy;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

//@Service
@RequiredArgsConstructor
public class UrgencyPriceStrategy implements PricingStrategy{

    private  final  PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(PricingSnapshot snapshot, PricingContext pricingContext) {
        BigDecimal price = pricingStrategy.calculatePrice(snapshot,pricingContext);
        LocalDate today = LocalDate.now();
        long daysUntilCheckIn = ChronoUnit.DAYS.between(today, pricingContext.getStartDate());

        if (daysUntilCheckIn <= 2) {
            price = price.multiply(BigDecimal.valueOf(1.30));
        } else if (daysUntilCheckIn <= 5) {
            price = price.multiply(BigDecimal.valueOf(1.15));
        }
        return price;
    }
}
