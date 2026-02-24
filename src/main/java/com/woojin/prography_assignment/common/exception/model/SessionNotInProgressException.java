package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class SessionNotInProgressException extends BusinessException {

    public SessionNotInProgressException() {
        super(ErrorCode.SESSION_NOT_IN_PROGRESS);
    }
}
