package com.SpringBoot.Sanjyot.AirbnbClone.repositories;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.GuestEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<GuestEntity,Long> {
}
