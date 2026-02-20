package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(ErrorCode errorCode,
                                      String customMessage) {
        super(errorCode, customMessage);
    }

    public DuplicateResourceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
