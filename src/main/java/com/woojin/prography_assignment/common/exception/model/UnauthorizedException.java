package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
