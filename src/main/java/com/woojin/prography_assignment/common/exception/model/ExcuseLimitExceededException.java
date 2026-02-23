package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class ExcuseLimitExceededException extends BusinessException {

    public ExcuseLimitExceededException(String customMessage) {
        super(ErrorCode.EXCUSE_LIMIT_EXCEEDED, customMessage);
    }
}
