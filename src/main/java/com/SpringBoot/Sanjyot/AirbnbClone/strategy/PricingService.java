package com.SpringBoot.Sanjyot.AirbnbClone.strategy;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingService {

    public BigDecimal calculateDynamicPrice(PricingSnapshot snapshot, PricingContext pricingContext){
        PricingStrategy pricingStrategy = new BasePriceStrategy();

        pricingStrategy = new SurgePriceStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPriceStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPriceStrategy(pricingStrategy);
        pricingStrategy = new HolidayPriceStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(snapshot,pricingContext);
    }
}
