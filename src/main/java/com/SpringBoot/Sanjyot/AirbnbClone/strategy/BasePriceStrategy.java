package com.SpringBoot.Sanjyot.AirbnbClone.strategy;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.Inventory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//@Service
@AllArgsConstructor
public class BasePriceStrategy implements PricingStrategy{

    @Override
    public BigDecimal calculatePrice(PricingSnapshot snapshot, PricingContext pricingContext) {
        BigDecimal price = snapshot.basePrice();
        return price;
    }
}
