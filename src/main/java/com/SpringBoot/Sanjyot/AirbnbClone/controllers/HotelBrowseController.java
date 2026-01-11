package com.SpringBoot.Sanjyot.AirbnbClone.controllers;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelInfoDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelSearchRequestDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos.HotelWithAvailableRoomsDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.InventoryRepository;
import com.SpringBoot.Sanjyot.AirbnbClone.services.HotelService;
import com.SpringBoot.Sanjyot.AirbnbClone.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotels/search")
public class HotelBrowseController {

    private final HotelService hotelService;
    private final InventoryService inventoryService;

    @PostMapping()
    public ResponseEntity<Slice<HotelWithAvailableRoomsDTO>> searchHotels(@RequestBody HotelSearchRequestDTO hotelSearchRequestDTO,
                                                                          @PageableDefault(page = 0,size = 10)Pageable pageable){
        Slice<HotelWithAvailableRoomsDTO> slice = inventoryService.searchHotels(hotelSearchRequestDTO,pageable);
        return ResponseEntity.ok(slice);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDTO> getHotelInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelInfo(hotelId));
    }
}
