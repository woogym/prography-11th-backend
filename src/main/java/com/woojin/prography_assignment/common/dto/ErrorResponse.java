package com.woojin.prography_assignment.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public record ErrorResponse(String code,
                            String message) {

}
