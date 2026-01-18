package com.SpringBoot.Sanjyot.AirbnbClone.controllers;

import com.SpringBoot.Sanjyot.AirbnbClone.advice.ApiResponse;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.BookingDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelReportDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.BookingStatus;
import com.SpringBoot.Sanjyot.AirbnbClone.services.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels")
public class HotelController {

    private final HotelService hotelService;
//    private final BookingService bookingService;

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

    @GetMapping
    private ResponseEntity<List<HotelDTO>> getAllHotels(){
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}/bookings")
    private ResponseEntity<List<BookingDTO>> getAllBookingsByHotelId(@PathVariable Long hotelId,
                                                                     @RequestParam(required = false) BookingStatus status){
        return ResponseEntity.ok(hotelService.getAllBookingsByHotelId(hotelId, status));
    }

    @GetMapping("/{hotelId}/reports")
    private ResponseEntity<HotelReportDTO> getHotelReports(@PathVariable Long hotelId,
                                                                 @RequestParam(required = false) LocalDate startDate,
                                                                 @RequestParam(required = false) LocalDate endDate){
        if (startDate == null)  startDate = LocalDate.now().minusMonths(1);
        if (endDate == null)  endDate = LocalDate.now();
        return ResponseEntity.ok(hotelService.getHotelReports(hotelId, startDate, endDate));
    }

}
