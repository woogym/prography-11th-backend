package com.woojin.prography_assignment.qr.controller;

import com.woojin.prography_assignment.common.dto.ApiResponse;
import com.woojin.prography_assignment.qr.dto.response.QrResponse;
import com.woojin.prography_assignment.qr.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/sessions")
public class AdminQrController {

    private final QrService qrService;

    @PostMapping("/{sessionId}/qrcodes")
    public ResponseEntity<ApiResponse<QrResponse>> createQrCode(@PathVariable Long sessionId) {

        QrResponse response = qrService.createQrcode(sessionId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
