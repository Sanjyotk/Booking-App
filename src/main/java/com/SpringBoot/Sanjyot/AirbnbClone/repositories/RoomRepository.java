package com.SpringBoot.Sanjyot.AirbnbClone.repositories;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.RoomEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity,Long> {

    List<RoomEntity> findByHotelId(Long hotelId);
}
