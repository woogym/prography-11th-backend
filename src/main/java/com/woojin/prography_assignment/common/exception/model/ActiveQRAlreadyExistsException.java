package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class ActiveQRAlreadyExistsException extends BusinessException {

    public ActiveQRAlreadyExistsException() {
        super(ErrorCode.QR_ALREADY_ACTIVE);
    }
}
