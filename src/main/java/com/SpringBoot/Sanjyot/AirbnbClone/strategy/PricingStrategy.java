package com.SpringBoot.Sanjyot.AirbnbClone.strategy;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(PricingSnapshot snapshot, PricingContext pricingContext);
}
