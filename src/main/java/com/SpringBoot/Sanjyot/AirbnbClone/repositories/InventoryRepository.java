package com.SpringBoot.Sanjyot.AirbnbClone.repositories;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.HotelSearchRequestDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos.AvailableRoomRow;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.HotelEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.Inventory;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.RoomEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    void deleteByRoom(RoomEntity room);

    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.city = :city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount)>= :roomsCount
            GROUP BY i.hotel, i.room
            HAVING COUNT(i.date) = :daysCount
            """)
    Page<HotelEntity> searchHotels(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("daysCount") Long daysCount,
//            HotelSearchRequestDTO hotelSearchRequestDTO,
            Pageable pageable);

//    @Query("""
//    SELECT new com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos.AvailableRoomRow(
//        i.hotel.id,
//        i.hotel.name,
//        i.hotel.city,
//        i.room.id,
//        i.room.type,
//        i.price,
//        MIN(i.totalCount - i.bookedCount - i.reservedCount)
//    )
//    FROM Inventory i
//    WHERE i.city = :city
//      AND i.date BETWEEN :startDate AND :endDate
//      AND i.closed = false
//
//      AND (i.room.type = :roomType OR :roomType IS NULL)
//      AND (:minPrice IS NULL OR i.price >= :minPrice)
//      AND (:maxPrice IS NULL OR i.price <= :maxPrice)
//
//      AND (i.totalCount - i.bookedCount -i.reservedCount) >= :roomsCount
//    GROUP BY
//        i.hotel.id, i.hotel.name, i.hotel.city,
//        i.room.id, i.room.type, i.price
//    HAVING COUNT(DISTINCT i.date) = :daysCount
//""")
//    Slice<AvailableRoomRow> searchHotelsAndRooms(
//            @Param("city") String city,
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate,
//            @Param("roomsCount") Integer roomsCount,
//            @Param("daysCount") Long daysCount,
//            @Param("roomType") String roomType,
//            @Param("maxPrice") BigDecimal maxPrice,
//            @Param("minPrice") BigDecimal minPrice,
////            HotelSearchRequestDTO hotelSearchRequestDTO,
//            Pageable pageable);

    @Query("""
    SELECT new com.SpringBoot.Sanjyot.AirbnbClone.dto.searchDtos.AvailableRoomRow(
        i.hotel.id,
        i.hotel.name,
        i.hotel.city,
        i.room.id,
        i.room.type,
        i.room.basePrice,
        MIN(i.totalCount - i.bookedCount - i.reservedCount),
        MAX(i.surgeFactor)
    )
    FROM Inventory i
    WHERE i.city = :city
      AND i.date BETWEEN :startDate AND :endDate
      AND i.closed = false
      AND (:roomType IS NULL OR i.room.type = :roomType)
      AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
    GROUP BY
        i.hotel.id,
        i.hotel.name,
        i.hotel.city,
        i.room.id,
        i.room.type,
        i.room.basePrice
    HAVING COUNT(i.id) = :daysCount
""")
    Slice<AvailableRoomRow> searchHotelsAndRooms(
            String city,
            LocalDate startDate,
            LocalDate endDate,
            Integer roomsCount,
            Long daysCount,
            String roomType,
            Pageable pageable
    );


    @Query("""
            SELECT MIN(i.totalCount - i.bookedCount - i.reservedCount)
            FROM Inventory i
            WHERE i.hotel.id = :hotelId
                AND i.room.id = :roomId
                AND i.closed = false
                AND i.date >= :checkInDate
                AND i.date < :checkOutDate
            """)
    Integer availableRooms (
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    @Modifying
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        UPDATE Inventory i
        SET i.reservedCount = i.reservedCount + :roomsCount
        WHERE i.hotel.id = :hotelId
          AND i.room.id = :roomId
          AND i.date >= :checkInDate
          AND i.date < :checkOutDate
    """)
    void reserveRooms(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomsCount") Integer roomsCount
    );


    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.reservedCount = i.reservedCount - :roomsCount
        WHERE i.hotel.id = :hotelId
          AND i.room.id = :roomId
          AND i.date >= :checkInDate
          AND i.date < :checkOutDate
    """)
    void releaseReservedRooms(
            Long hotelId,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer roomsCount
    );

    @Query("""
        SELECT MAX(i.surgeFactor)
        FROM Inventory i
        WHERE i.hotel.id = :hotelId
          AND i.room.id = :roomId
          AND i.closed = false
          AND i.date >= :checkInDate
          AND i.date < :checkOutDate
    """)
    BigDecimal maxSurgeFactor(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET
          i.bookedCount = i.bookedCount + :rooms,
          i.reservedCount = i.reservedCount - :rooms
        WHERE i.hotel.id = :hotelId
          AND i.room.id = :roomId
          AND i.date >= :checkInDate
          AND i.date < :checkOutDate
    """)
    void confirmBooking(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("rooms") Integer rooms
    );
}
