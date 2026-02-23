package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class InsufficientDepositException extends BusinessException {

    public InsufficientDepositException(String customMessage) {
        super(ErrorCode.DEPOSIT_INSUFFICIENT, customMessage);
    }
}
