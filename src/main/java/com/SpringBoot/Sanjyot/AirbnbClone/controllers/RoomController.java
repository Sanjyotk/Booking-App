package com.SpringBoot.Sanjyot.AirbnbClone.controllers;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.RoomDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createNewRoom(@PathVariable Long hotelId, @RequestBody RoomDTO roomDTO){
        return new ResponseEntity<>(roomService.createNewRoom(hotelId,roomDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> allRoomsInHotel(@PathVariable Long hotelId){
        return ResponseEntity.ok(roomService.getAllRoomsInHotel(hotelId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId){
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long roomId){
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

}
