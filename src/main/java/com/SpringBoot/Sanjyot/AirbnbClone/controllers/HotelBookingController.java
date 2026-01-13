package com.SpringBoot.Sanjyot.AirbnbClone.controllers;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.BookingDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.BookingRequest;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.GuestDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.PaymentDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.services.BookingService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/reserve")
    public ResponseEntity<BookingDTO> initialiseBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDTO> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guestDTOList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDTOList));
    }

//    @PostMapping("/{bookingId}/payment")
//    public ResponseEntity<PaymentDTO> makePayment(@PathVariable Long bookingId){
//        return ResponseEntity.ok(bookingService.makePayment(bookingId));
//    }

    @PostMapping("/{bookingId}/payment")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long bookingId){
        String sessionUrl = bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }

    @PostMapping("/{bookingId}/payment/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long bookingId){
        String status = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(Map.of("status",status));
    }

}
