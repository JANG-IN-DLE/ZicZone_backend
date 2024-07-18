package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.config.PayConfig;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.payment.PayState;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.payment.PaymentDTO;
import org.zerock.ziczone.exception.UnauthorizedException;
import org.zerock.ziczone.service.login.JwtService;
import org.zerock.ziczone.service.payment.PaymentService;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PayConfig payConfig;
    private final JwtService jwtService;

    @PostMapping("/confirm")
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody Map<String, Object> requestData) throws IOException, ParseException {
        // 토큰 검증
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !jwtService.validateToken(token.replace("Bearer ", ""), jwtService.extractUsername(token.replace("Bearer ", "")))) {
            throw new UnauthorizedException("Invalid token");
        }

        String orderId = (String) requestData.get("orderId");
        int amount = Integer.parseInt(String.valueOf(requestData.get("amount")));
        String paymentKey = (String) requestData.get("paymentKey");
        Long userId = Long.parseLong(String.valueOf(requestData.get("userId")));

        // 사용자 ID 검증
        Long extractedUserId = jwtService.extractUserId(token.replace("Bearer ", ""));
        log.debug("Extracted userId from token: {}", extractedUserId);
        log.debug("Received userId from request: {}", userId);

        if (!extractedUserId.equals(userId)) {
            throw new UnauthorizedException("Invalid user ID");
        }

        // PersonalUser 객체 생성 (실제로는 데이터베이스에서 가져오는 로직이 필요할 수 있음)
        PersonalUser personalUser = PersonalUser.builder()
                .personalId(userId)
                .build();

        // PaymentDTO 생성
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentKey(paymentKey)
                .personalUser(personalUser)
                .payDate(LocalDateTime.now())
                .payState(PayState.SUCCESS)
                .build();

        // 결제 정보 저장
        Payment payment = paymentService.savePayment(paymentDTO);

        // 클라이언트에게 반환할 정보 구성
        JSONObject response = new JSONObject();
        response.put("amount", payment.getAmount());
        response.put("berryPoint", payment.getBerryPoint());

        return ResponseEntity.ok(response);
    }
}
