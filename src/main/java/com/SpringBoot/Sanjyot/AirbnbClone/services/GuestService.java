package com.SpringBoot.Sanjyot.AirbnbClone.services;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.GuestDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.GuestEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.exception.ResourceNotFoundException;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    public GuestDTO updateGuests(Long guestId, GuestDTO guestDTO) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GuestEntity guest = guestRepository.findById(guestId).orElseThrow(() -> new ResourceNotFoundException("guest with id not found"));

        if (!guest.getUser().getId().equals(user.getId())){
            throw new IllegalStateException("Guest not with current logged in user");
        }

        if(guestDTO.getName() != null) guest.setName(guestDTO.getName());
        if(guestDTO.getGender() != null) guest.setGender(guestDTO.getGender());
        if (guestDTO.getAge() != null) guest.setAge(guestDTO.getAge());

        return modelMapper.map(guestRepository.save(guest), GuestDTO.class);
    }

    public void deleteGuests(List<Long> guestIds) {
        guestRepository.deleteAllById(guestIds);
    }
}
