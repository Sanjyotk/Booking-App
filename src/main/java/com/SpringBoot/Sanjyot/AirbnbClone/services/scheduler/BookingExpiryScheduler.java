package com.SpringBoot.Sanjyot.AirbnbClone.services.scheduler;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.BookingDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.BookingRequest;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.GuestDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.*;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.BookingStatus;
import com.SpringBoot.Sanjyot.AirbnbClone.exception.ResourceNotFoundException;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingExpiryScheduler {

    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    @Scheduled(fixedRate = 60_000) // every 1 minute
    public void expireOldReservations() {

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(60);

        List<BookingEntity> expiredBookings =
                bookingRepository.findExpiredReservations(expiryTime);

        for (BookingEntity booking : expiredBookings) {

            // 1️⃣ Release inventory
            inventoryRepository.releaseReservedRooms(
                    booking.getHotel().getId(),
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getRoomsCount()
            );

            // 2️⃣ Mark booking expired
            booking.setBookingStatus(BookingStatus.CANCELLED);
        }
    }
}
