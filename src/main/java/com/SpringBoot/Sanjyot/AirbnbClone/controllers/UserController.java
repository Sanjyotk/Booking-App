package com.SpringBoot.Sanjyot.AirbnbClone.controllers;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.BookingDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.GuestDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.ProfileUpdateRequestDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.UserRepository;
import com.SpringBoot.Sanjyot.AirbnbClone.services.BookingService;
import com.SpringBoot.Sanjyot.AirbnbClone.services.GuestService;
import com.SpringBoot.Sanjyot.AirbnbClone.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDTO profileUpdateRequestDto) {
        userService.updateProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings(){
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @PatchMapping("/guests/{guestId}")
    public ResponseEntity<GuestDTO> updateGuests(@PathVariable Long guestId,
                                                 @RequestBody GuestDTO guestDTO){
        return ResponseEntity.ok(guestService.updateGuests(guestId,guestDTO));
    }

    @DeleteMapping("/guests/{guestId}")
    public ResponseEntity<Void> deleteGuests(@PathVariable List<Long> guestIds){
        guestService.deleteGuests(guestIds);
        return ResponseEntity.noContent().build();
    }
}
