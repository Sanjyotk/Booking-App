package com.SpringBoot.Sanjyot.AirbnbClone.controllers;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.InventoryDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.UpdateInventoryDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<InventoryDTO>> getAllInventoryByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<InventoryDTO>> getAllInventoryByHotel(@PathVariable Long hotelId){
        return ResponseEntity.ok(inventoryService.getAllInventoryByHotel(hotelId));
    }

    @PatchMapping("/room/{roomId}")
    public ResponseEntity<Void> updateInventoryByRoom(@PathVariable Long roomId,
                                                      @RequestBody UpdateInventoryDTO updateRoomInventoryDTO){
        inventoryService.updateInventoryByRoom(roomId, updateRoomInventoryDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> updateInventoryByHotel(@PathVariable Long hotelId,
                                                      @RequestBody UpdateInventoryDTO updateHotelInventoryDTO){
        inventoryService.updateInventoryByHotel(hotelId,updateHotelInventoryDTO);
        return ResponseEntity.noContent().build();
    }
}
