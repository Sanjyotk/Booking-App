package com.SpringBoot.Sanjyot.AirbnbClone.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message){
        super(message);
    }
}
