package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode,
                                   String customMessage) {
        super(errorCode, customMessage);
    }

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
