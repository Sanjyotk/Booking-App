package com.SpringBoot.Sanjyot.AirbnbClone.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
//    private LocalDateTime timestamp;
    private Integer Status;
    private String message;
    private String error;
}
