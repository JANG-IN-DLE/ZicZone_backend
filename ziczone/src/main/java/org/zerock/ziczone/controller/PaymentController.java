package org.zerock.ziczone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.payment.PaymentDTO;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ObjectMapper objectMapper;

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, Object> requestBody) {
        String url = "";


//        Map<String, Object> responseBody;
//        return ResponseEntity.ok(responseBody);
    }
}
