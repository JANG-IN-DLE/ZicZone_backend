package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.ziczone.config.PayConfig;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.payment.PayState;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.payment.PaymentDTO;
import org.zerock.ziczone.exception.UnauthorizedException;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.service.login.JwtService;
import org.zerock.ziczone.service.payment.PaymentService;
import org.zerock.ziczone.service.payment.TossPayService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final TossPayService tossPayService;
    private final PersonalUserRepository personalUserRepository;
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
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);

        // PaymentDTO 생성
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentKey(paymentKey)
                .personalUser(personalUser)
                .payDate(LocalDateTime.now())
                .payState(PayState.PENDING)
                .build();

        // 결제 정보 저장
        Payment savedPayment = paymentService.savePayment(paymentDTO);

        // 토스 페이 서버에 결제 승인 요청
        Map<String, Object> tossPayResponse;
        try {
            tossPayResponse = tossPayService.confirmPayment(orderId, paymentKey, amount);
        } catch (IOException e) {
            paymentService.failPayment(savedPayment.getPayId());
            throw e;
        }

        // 승인 후 받은 데이터와 기존 데이터 비교
        int responseAmount = (int) tossPayResponse.get("totalAmount");
        String responseOrderId = (String) tossPayResponse.get("orderId");
        String responsePaymentKey = (String) tossPayResponse.get("paymentKey");

        if (responseAmount == amount && responseOrderId.equals(orderId) && responsePaymentKey.equals(paymentKey)) {
            // 승인 완료 처리
            Payment approvedPayment = paymentService.approvePayment(savedPayment.getPayId(), amount / 10);

            // 클라이언트에게 반환할 정보 구성
            JSONObject response = new JSONObject();
            response.put("amount", approvedPayment.getAmount());
            response.put("berryPoint", approvedPayment.getBerryPoint());

            return ResponseEntity.ok(response);
        } else {
            // 데이터 불일치 시 결제 상태를 실패로 설정
            paymentService.failPayment(savedPayment.getPayId());
            throw new IllegalStateException("Payment information mismatch after approval");
        }
    }
}

/*
    결제 요청을 보내고
    승인 요청을 보낼 때
    승인 요청 api를 보내기 전에 미리 데이터베이스에 값을 미리 저장
 */
