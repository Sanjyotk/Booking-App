package com.SpringBoot.Sanjyot.AirbnbClone.services;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelSearchRequestDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos.AvailableRoomDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos.AvailableRoomRow;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos.HotelWithAvailableRoomsDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.HotelEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.Inventory;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.RoomEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.InventoryRepository;
import com.SpringBoot.Sanjyot.AirbnbClone.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final PricingService pricingService;

    public void initialiseRoomForAYear(RoomEntity room){
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        while(!today.isAfter(endDate)){
            Inventory inventory = Inventory.builder()
                    .room(room)
                    .hotel(room.getHotel())
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .bookedCount(0)
                    .totalCount(room.getTotalCount())
                    .date(today)
                    .city(room.getHotel().getCity())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
            today = today.plusDays(1);
        }
    }

    public void deleteAllRooms(RoomEntity room){
        inventoryRepository.deleteByRoom(room);
    }

    public Slice<HotelWithAvailableRoomsDTO> searchHotels(HotelSearchRequestDTO hotelSearchRequestDTO, Pageable pageable) {
        Long daysCount = ChronoUnit.DAYS.between(hotelSearchRequestDTO.getStartDate(), hotelSearchRequestDTO.getEndDate()) + 1;
        Slice<AvailableRoomRow> availableRoomRows = inventoryRepository.searchHotelsAndRooms(
                hotelSearchRequestDTO.getCity(),
                hotelSearchRequestDTO.getStartDate(),
                hotelSearchRequestDTO.getEndDate(),
                hotelSearchRequestDTO.getRoomsCount(),
                daysCount,
                hotelSearchRequestDTO.getRoomType(),
//                hotelSearchRequestDTO.getMaxPrice(),
//                hotelSearchRequestDTO.getMinPrice(),
                pageable);

        Map<Long, HotelWithAvailableRoomsDTO> groupedHotelAndRoom = new LinkedHashMap<>();

        for (AvailableRoomRow row : availableRoomRows.getContent()) {
            PricingSnapshot snapshot = new PricingSnapshot(
                    row.basePrice(),
                    row.surgeFactor(),
                    row.remainingRooms(),
                    daysCount
            );


            PricingContext context = PricingContext.builder()
                    .startDate(hotelSearchRequestDTO.getStartDate())
                    .endDate(hotelSearchRequestDTO.getEndDate())
                    .daysCount(daysCount)
                    .roomsRequested(hotelSearchRequestDTO.getRoomsCount())
                    .build();

            BigDecimal computedPrice = pricingService.calculateDynamicPrice(snapshot, context);

            // in-memory price filter
            if (hotelSearchRequestDTO.getMinPrice() != null && computedPrice.compareTo(hotelSearchRequestDTO.getMinPrice()) < 0) continue;
            if (hotelSearchRequestDTO.getMaxPrice() != null && computedPrice.compareTo(hotelSearchRequestDTO.getMaxPrice()) > 0) continue;

            groupedHotelAndRoom.computeIfAbsent(
                    row.hotelId(),
                    id -> new HotelWithAvailableRoomsDTO(
                            row.hotelId(),
                            row.hotelName(),
                            row.hotelCity(),
                            new ArrayList<>()
                    )
            ).rooms().add(
                    new AvailableRoomDTO(
                            row.roomId(),
                            row.roomType(),
                            computedPrice,
                            row.remainingRooms()
                    )
            );

        }

        return new SliceImpl<>(
                new ArrayList<>(groupedHotelAndRoom.values()),
                pageable,
                availableRoomRows.hasNext()
        );
    }
}
