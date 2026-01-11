package com.SpringBoot.Sanjyot.AirbnbClone.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime timeStamp;
    private T data;
    private ApiErrorResponse apiErrorResponse;

    public ApiResponse() {
        this.timeStamp = LocalDateTime.now();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse(ApiErrorResponse apiErrorResponse) {
        this();
        this.apiErrorResponse = apiErrorResponse;
    }


}
