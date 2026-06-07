package com.firstclub.interview.dto;

public record ApiErrorResponse(int status, String error) {
    public static ApiErrorResponse of(int status, String error) {
        return new ApiErrorResponse(status, error);
    }
}
