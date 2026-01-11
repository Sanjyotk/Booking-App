package com.SpringBoot.Sanjyot.AirbnbClone.services;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.*;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.*;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.BookingStatus;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.PaymentStatus;
import com.SpringBoot.Sanjyot.AirbnbClone.exception.ResourceNotFoundException;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.*;
import com.SpringBoot.Sanjyot.AirbnbClone.security.JWTService;
import com.SpringBoot.Sanjyot.AirbnbClone.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final PricingService pricingService;
    private final PaymentRepository paymentRepository;
    private final JWTService jwtService;

    @Transactional
    public BookingDTO initialiseBooking(BookingRequest bookingRequest) {
        HotelEntity hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(()-> new ResourceNotFoundException("Hotel not found"));
        RoomEntity room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(()-> new ResourceNotFoundException("Room not found"));

        long nights = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());

        Integer available = inventoryRepository.availableRooms(
                bookingRequest.getHotelId(),
                bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate()
        );

        BigDecimal surgeFactor =
                inventoryRepository.maxSurgeFactor(
                        bookingRequest.getHotelId(),
                        bookingRequest.getRoomId(),
                        bookingRequest.getCheckInDate(),
                        bookingRequest.getCheckOutDate()
                );

        PricingSnapshot snapshot = new PricingSnapshot(
                room.getBasePrice(),
                surgeFactor,
                available,   // MIN remaining rooms across days
                nights
        );

        PricingContext context = PricingContext.builder()
                .startDate(bookingRequest.getCheckInDate())
                .endDate(bookingRequest.getCheckOutDate())
                .roomsRequested(bookingRequest.getRoomsRequest())
                .daysCount(nights)
                .build();

        BigDecimal pricePerNight = pricingService.calculateDynamicPrice(snapshot, context);
        BigDecimal totalPrice =pricePerNight.multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(bookingRequest.getRoomsRequest()));

        if (available == null || available < bookingRequest.getRoomsRequest()) {
            throw new IllegalStateException("Requested rooms not available");
        }

        // Reserve inventory (CRITICAL)
        inventoryRepository.reserveRooms(
                bookingRequest.getHotelId(),
                bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsRequest()
        );

        //temp
        UserEntity user = userRepository.findById(1L).orElseThrow(()->new ResourceNotFoundException("user not found"));

        BookingEntity booking = BookingEntity.builder()
                .hotel(hotel)
                .room(room)
                .user(user)
                .roomsCount(bookingRequest.getRoomsRequest())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .pricePerNight(pricePerNight)
                .totalPrice(totalPrice)
                .bookingStatus(BookingStatus.RESERVED)
                .build();

        bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDTO.class);
    }

    @Transactional
    public BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking id not found"));
        BookingStatus status = booking.getBookingStatus();

        UserEntity user1 = (UserEntity) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        if (!booking.getUser().equals(user1)){
            throw new IllegalStateException("Booking id not with current user");
        }

        if (status != BookingStatus.RESERVED && status != BookingStatus.GUESTS_ADDED) {
            throw new IllegalStateException(
                    "Guests can only be added when booking is RESERVED or GUESTS_ADDED"
            );
        }

        int maxGuests = booking.getRoomsCount() * booking.getRoom().getCapacity();
        if (guestDTOList.size() > maxGuests) {
            throw new IllegalStateException("Guest count exceeds room capacity");
        }

        UserEntity user = booking.getUser();

        Set<GuestEntity> guestEntities = guestDTOList.stream()
                .map(dto -> {
                    GuestEntity guest = modelMapper.map(dto, GuestEntity.class);
                    guest.setUser(user);
                    return guest;
                })
                .map(guestRepository::save)
                .collect(Collectors.toSet());

        booking.getGuests().addAll(guestEntities);
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        return modelMapper.map(booking, BookingDTO.class);
    }

    @Transactional
    public PaymentDTO makePayment(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.GUESTS_ADDED) {
            throw new IllegalStateException("Payment not allowed in current booking state");
        }

        PaymentEntity payment = PaymentEntity.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        boolean paymentSuccess = true; // mock gateway response

        if (paymentSuccess) {

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(System.currentTimeMillis()); // mock txn id

            // FINALIZE INVENTORY
            inventoryRepository.confirmBooking(
                    booking.getHotel().getId(),
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getRoomsCount()
            );

            booking.setBookingStatus(BookingStatus.CONFIRMED);

        } else {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
        }
//        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
        bookingRepository.save(booking);

        return modelMapper.map(payment, PaymentDTO.class);

    }
}
