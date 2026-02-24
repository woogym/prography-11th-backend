package com.woojin.prography_assignment.common.exception.model;

import com.woojin.prography_assignment.common.exception.ErrorCode;

public class AttendanceAlreadyCheckedException extends BusinessException {

    public AttendanceAlreadyCheckedException() {
        super(ErrorCode.ATTENDANCE_ALREADY_CHECKED);
    }
}
