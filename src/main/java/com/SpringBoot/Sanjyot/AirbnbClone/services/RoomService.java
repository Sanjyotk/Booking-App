package com.SpringBoot.Sanjyot.AirbnbClone.services;


import com.SpringBoot.Sanjyot.AirbnbClone.dto.RoomDTO;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.HotelEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.RoomEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.exception.ResourceNotFoundException;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.HotelRepository;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.RoomRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;

    public RoomDTO createNewRoom(Long hotelId,RoomDTO roomDto){
        HotelEntity hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel with id:"+hotelId+" not found"));
        RoomEntity room = modelMapper.map(roomDto,RoomEntity.class);
        room.setHotel(hotel);
        roomRepository.save(room);

        if (room.getHotel().getActive()){
            inventoryService.initialiseRoomForAYear(room);
        }

        return modelMapper.map(room,RoomDTO.class);
    }

    public List<RoomDTO> getAllRoomsInHotel(Long hotelId) {
        HotelEntity hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel with id:"+hotelId+" not found"));
        return hotel.getRooms().stream().map(roomEntity -> modelMapper.map(roomEntity, RoomDTO.class)).toList();
    }

    public RoomDTO getRoomById(Long roomId) {
        RoomEntity room = roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("room not found"));
        return modelMapper.map(room,RoomDTO.class);
    }

    @Transactional
    public void deleteRoomById(Long roomId) {
        RoomEntity room = roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("room not found"));
        inventoryService.deleteAllRooms(room);
        roomRepository.deleteById(roomId);
    }

    @Transactional
    public RoomDTO updateRoomById(Long hotelId, Long roomId, Map<String,Object> roomDto) {
        HotelEntity hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel with id:"+hotelId+" not found"));
        RoomEntity room = roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("room not found"));
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!hotel.getOwner().getId().equals(user.getId())){
            throw new IllegalStateException("Hotel id not with owner id");
        }

        //reflection
        roomDto.forEach((field,value)->{
            Field fieldToBeUpdated = ReflectionUtils.getRequiredField(RoomEntity.class,field);
            fieldToBeUpdated.setAccessible(true);
            ReflectionUtils.setField(fieldToBeUpdated,room,value);
        });
        roomRepository.save(room);
        return modelMapper.map(room, RoomDTO.class);
    }
}