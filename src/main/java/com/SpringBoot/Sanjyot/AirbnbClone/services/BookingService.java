package com.SpringBoot.Sanjyot.AirbnbClone.services;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.*;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingContext;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.pricing.PricingSnapshot;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.*;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.BookingStatus;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.PaymentStatus;
import com.SpringBoot.Sanjyot.AirbnbClone.exception.ResourceNotFoundException;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.*;
import com.SpringBoot.Sanjyot.AirbnbClone.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCancelParams;
import com.stripe.param.RefundCreateParams;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final CheckoutService checkoutService;

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

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

        UserEntity user1 = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(user1);
        System.out.println(booking.getUser());
        if (!booking.getUser().getId().equals(user1.getId())) {
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

    @Transactional
    public String initiatePayment(Long bookingId) {

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.GUESTS_ADDED) {
            throw new IllegalStateException("Payment not allowed in current booking state");
        }

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Booking id not with current user");
        }

        String paymentSessionUrl = checkoutService.getCheckoutSession(booking,
                "http://localhost:8080/payments/success",
                "http://localhost:8080/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return paymentSessionUrl;

    }

    @Transactional
    public void capturePayment(Event event) {
//        if ("checkout.session.completed".equals(event.getType())) {
//
//            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
//            if (session == null) return;
//
//            String sessionId = session.getId();
//            BookingEntity booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() -> new ResourceNotFoundException("Booking with id not found"));
//            booking.setBookingStatus(BookingStatus.CONFIRMED);
//            bookingRepository.save(booking);
//
//            // FINALIZE INVENTORY
//            inventoryRepository.confirmBooking(
//                    booking.getHotel().getId(),
//                    booking.getRoom().getId(),
//                    booking.getCheckInDate(),
//                    booking.getCheckOutDate(),
//                    booking.getRoomsCount()
//            );
//            log.warn("Successfully confirmed the booking for Booking ID: {}", booking.getId());
//
//        } else {
//            log.warn("Unhandled event type: {}", event.getType());
//        }

        log.warn("Webhook event received: {}", event.getType());

        if (!"checkout.session.completed".equals(event.getType())) {
            return;
        }

        Object dataObject = event.getData().getObject();

        String bookingIdStr = null;
        String paymentIntentId = null;

        // ✅ Case 1: Stripe SDK deserialized to Session
        if (dataObject instanceof Session session) {
            bookingIdStr = session.getMetadata().get("bookingId");
            paymentIntentId = session.getPaymentIntent();
        }
        // ✅ Case 2: Raw map (deserialization failed or partial)
        else if (dataObject instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, String> metadata =
                    (Map<String, String>) map.get("metadata");

            if (metadata != null) {
                bookingIdStr = metadata.get("bookingId");
            }
            paymentIntentId = (String) map.get("payment_intent");
        }

        if (bookingIdStr == null) {
            log.error("❌ bookingId missing in checkout.session.completed event");
            return;
        }

        Long bookingId = Long.valueOf(bookingIdStr);

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // 🔐 Idempotency guard
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            log.warn("Booking {} already confirmed, skipping", bookingId);
            return;
        }

        booking.setPaymentIntentId(paymentIntentId);
        // ✅ Confirm booking
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // ✅ Finalize inventory
        inventoryRepository.confirmBooking(
                booking.getHotel().getId(),
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount()
        );

        log.warn(
                "✅ Booking CONFIRMED. bookingId={}",
                bookingId
        );
    }

//    @Transactional
    public String cancelBooking(Long bookingId) {

        UserEntity user = (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Booking does not belong to user");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        try {
            RefundCreateParams refundParams =
                    RefundCreateParams.builder()
                            .setPaymentIntent(booking.getPaymentIntentId())
                            .build();

            log.warn("STEP 1: Entered cancelBooking");

            Refund.create(refundParams);

            log.warn("STEP 2: Refund call succeeded");

            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            log.warn("STEP 3: Booking updated");

            inventoryRepository.cancelBooking(
                    booking.getHotel().getId(),
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getRoomsCount()
            );

            return "Refund initiated";

        } catch (StripeException e) {
            throw new RuntimeException("Refund failed", e);
        }
    }
}