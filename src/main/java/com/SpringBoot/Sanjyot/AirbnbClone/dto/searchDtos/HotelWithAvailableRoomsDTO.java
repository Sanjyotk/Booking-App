package com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos;

import java.util.List;

public record HotelWithAvailableRoomsDTO(
        Long hotelId,
        String name,
        String city,
        List<AvailableRoomDTO> rooms
) {}