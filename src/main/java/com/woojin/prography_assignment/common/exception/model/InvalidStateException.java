package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class InvalidStateException extends BusinessException {

    public InvalidStateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidStateException(ErrorCode errorCode,
                                 String customMessage) {
        super(errorCode, customMessage);
    }
}
