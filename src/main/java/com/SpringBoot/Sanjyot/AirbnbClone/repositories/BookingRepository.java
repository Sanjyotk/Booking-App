package com.SpringBoot.Sanjyot.AirbnbClone.repositories;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelReportDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.BookingEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.HotelEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,Long> {

    @Query("""
        SELECT b
        FROM BookingEntity b
        WHERE b.bookingStatus = 'RESERVED'
          AND b.createdAt < :expiryTime
    """)
    List<BookingEntity> findExpiredReservations(@Param("expiryTime") LocalDateTime expiryTime);

    Optional<BookingEntity> findByPaymentSessionId(String sessionId);

    List<BookingEntity> findByHotelId(Long ownerId);

    List<BookingEntity> findByHotelIdAndCreatedAtBetween(Long hotelId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<BookingEntity> findByUserId(Long userId);
}
