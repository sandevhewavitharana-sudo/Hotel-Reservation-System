package com.hotelbooking.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final int status;
    private final String message;
    private final String error;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(int status, String message, String error) {
        this.status = status;
        this.error = error;
        this.message = message;
    }


}




