package com.firstclub.interview.dto;

public record ApiResponse<T>(int status, boolean success, T data, String error) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, true, data, null);
    }
    public static <T> ApiResponse<T> error(int status, String msg) {
        return new ApiResponse<>(status, false, null, msg);
    }
}
