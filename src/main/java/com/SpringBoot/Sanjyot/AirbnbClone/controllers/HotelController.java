package com.SpringBoot.Sanjyot.AirbnbClone.controllers;

import com.SpringBoot.Sanjyot.AirbnbClone.advice.ApiResponse;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.services.HotelService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels")
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDTO> createNewHotel(@RequestBody HotelDTO hotelDTO){
        return new ResponseEntity<>(hotelService.createNewHotel(hotelDTO), HttpStatus.CREATED);
    }

    @GetMapping("/owner/{ownerId}")
    private ResponseEntity<List<HotelDTO>> getAllHotelsOfOwner(@PathVariable Long ownerId){
        return ResponseEntity.ok(hotelService.getAllHotelsOfOwner(ownerId));
    }

    @GetMapping("/{id}")
    private ResponseEntity<HotelDTO> findHotelById(@PathVariable Long id){
        return ResponseEntity.ok(hotelService.findHotelById(id));
    }

    @PutMapping("/{id}")
    private ResponseEntity<HotelDTO> updateHotelById(@PathVariable Long id, @RequestBody HotelDTO hotelDTO){
        return ResponseEntity.ok(hotelService.updateHotelById(id, hotelDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> changeHotelStatus(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(hotelService.activateHotelStatus(id)));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Boolean> deleteHotelById(@PathVariable Long id){
        return ResponseEntity.ok(hotelService.deleteHotelById(id));
    }

}
