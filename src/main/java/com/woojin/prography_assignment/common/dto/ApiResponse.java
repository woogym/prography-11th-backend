package com.woojin.prography_assignment.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                null,
                new ErrorResponse(errorCode.getCode(), errorCode.getMessage())
        );
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode, String customMessage) {
        return new ApiResponse<>(
                false,
                null,
                new ErrorResponse(errorCode.getCode(), customMessage)
        );
    }
}
