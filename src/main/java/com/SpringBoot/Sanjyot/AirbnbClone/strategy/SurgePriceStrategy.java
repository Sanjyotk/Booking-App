package com.SpringBoot.Sanjyot.AirbnbClone.strategy;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.Inventory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//@Service
@AllArgsConstructor
public class SurgePriceStrategy implements PricingStrategy{

    private final PricingStrategy pricingStrategy;


    @Override
    public BigDecimal calculatePrice(PricingSnapshot snapshot, PricingContext pricingContext) {
        BigDecimal price = pricingStrategy.calculatePrice(snapshot,pricingContext);
        return price.multiply(snapshot.surgeFactor());
    }
}
