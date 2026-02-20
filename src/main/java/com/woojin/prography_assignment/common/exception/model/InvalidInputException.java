package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class InvalidInputException extends BusinessException {

    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidInputException(ErrorCode errorCode,
                                 String customMessage) {
        super(errorCode, customMessage);
    }
}
