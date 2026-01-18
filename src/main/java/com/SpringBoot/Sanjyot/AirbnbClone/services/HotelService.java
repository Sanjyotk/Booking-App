package com.SpringBoot.Sanjyot.AirbnbClone.services;

import com.SpringBoot.Sanjyot.AirbnbClone.dto.*;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.HotelEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.RoomEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.BookingStatus;
import com.SpringBoot.Sanjyot.AirbnbClone.exception.ResourceNotFoundException;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.HotelRepository;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.RoomRepository;
import com.SpringBoot.Sanjyot.AirbnbClone.security.JWTService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    private final BookingService bookingService;
    private final JWTService jwtService;
    private final UserService userService;

    public HotelDTO createNewHotel(HotelDTO hotelDTO){
        HotelEntity hotel = (modelMapper.map(hotelDTO, HotelEntity.class));
//        Long id = jwtService.getUserIdFromToken(refreshToken);
//        UserEntity user = userService.loadUserById(id);
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);
        hotel.setActive(false);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDTO.class);
    }

    public HotelDTO findHotelById(Long id){
        return modelMapper
                .map(hotelRepository.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("Hotel with id:"+id+"not found"))
                ,HotelDTO.class);
    }

    public List<HotelDTO> getAllHotelsOfOwner(Long id) {
        List<HotelEntity> hotelEntityList = hotelRepository.findByOwnerId(id);
        return hotelEntityList.stream()
                .map(hotelEntity -> modelMapper.map(hotelEntity, HotelDTO.class))
                .toList();
    }

    public HotelDTO updateHotelById(Long id, HotelDTO hotelDTO) {
        HotelEntity hotel = hotelRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Hotel with id:"+id+" not found"));
        Boolean isActive = hotel.getActive();
        modelMapper.map(hotelDTO,hotel);
        hotel.setId(id);
        hotel.setActive(isActive);
        hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Transactional
    public String activateHotelStatus(Long id) {
        HotelEntity hotel = hotelRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Hotel with id:"+id+" not found"));
        hotel.setActive(true);
//        hotelRepository.save(hotel);

        for (RoomEntity room: hotel.getRooms()){
            inventoryService.initialiseRoomForAYear(room);
        }

        return "The status of the Hotel is: "+hotel.getActive();
    }

    @Transactional
    public Boolean deleteHotelById(Long id) {
        HotelEntity hotel = hotelRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Hotel with id:"+id+" not found"));
        for (RoomEntity room: hotel.getRooms()){
            inventoryService.deleteAllRooms(room);
            roomRepository.delete(room);
        }
        hotelRepository.deleteById(id);
        return true;
    }

    public HotelInfoDTO getHotelInfo(Long hotelId) {
        HotelEntity hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel with id:"+hotelId+" not found"));
        List<RoomDTO> roomDTOList = hotel.getRooms().stream().map(room->modelMapper.map(room, RoomDTO.class)).toList();
        return new HotelInfoDTO(modelMapper.map(hotel, HotelDTO.class),roomDTOList);
    }

    public List<HotelDTO> getAllHotels() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<HotelEntity> hotelEntityList = hotelRepository.findByOwnerId(user.getId());
        return hotelEntityList.stream()
                .map(hotelEntity -> modelMapper.map(hotelEntity, HotelDTO.class))
                .toList();
    }

    public List<BookingDTO> getAllBookingsByHotelId(Long hotelId, BookingStatus status) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HotelEntity hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel id not found"));
        if (!hotel.getOwner().getId().equals(user.getId())){
            throw new IllegalStateException("hotel owner id is not same as logged in user id");
        }
        return bookingService.getAllBookingsByHotelId(hotelId,status);
    }

    public HotelReportDTO getHotelReports(Long hotelId, LocalDate startDate, LocalDate endDate) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HotelEntity hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel id not found"));
        if (!hotel.getOwner().getId().equals(user.getId())){
            throw new IllegalStateException("hotel owner id is not same as logged in user id");
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return bookingService.getHotelReports(hotelId,startDateTime,endDateTime);
        
    }
}
