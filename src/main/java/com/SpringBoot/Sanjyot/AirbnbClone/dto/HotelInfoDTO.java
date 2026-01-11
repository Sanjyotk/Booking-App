package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelInfoDTO {
    private HotelDTO hotelDTO;
    private List<RoomDTO> roomDTO;
}
