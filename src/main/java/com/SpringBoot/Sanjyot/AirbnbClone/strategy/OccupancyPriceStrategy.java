package com.SpringBoot.Sanjyot.AirbnbClone.strategy;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//@Service
@RequiredArgsConstructor
public class OccupancyPriceStrategy implements PricingStrategy{

    private final PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(PricingSnapshot snapshot, PricingContext pricingContext) {
        BigDecimal price = pricingStrategy.calculatePrice(snapshot,pricingContext);
        if (snapshot.remainingRooms() > 5){
            price = price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
