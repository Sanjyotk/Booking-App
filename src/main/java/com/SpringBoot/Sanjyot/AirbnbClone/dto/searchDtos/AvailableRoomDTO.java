package com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos;

import java.math.BigDecimal;

public record AvailableRoomDTO(
        Long id,
        String type,
        BigDecimal price,
        Integer remainingRooms
) {}