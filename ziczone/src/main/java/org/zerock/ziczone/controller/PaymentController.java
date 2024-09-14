package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.payment.PayState;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.payment.PaymentDTO;
import org.zerock.ziczone.exception.UnauthorizedException;
import org.zerock.ziczone.exception.mypage.PersonalNotFoundException;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;
import org.zerock.ziczone.security.JwtService;
import org.zerock.ziczone.service.payment.PaymentService;
import org.zerock.ziczone.service.payment.TossPayService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final TossPayService tossPayService;
    private final PersonalUserRepository personalUserRepository;
    private final PaymentService paymentService;
    private final JwtService jwtService;
    private final PaymentRepository paymentRepository;
    private final PayHistoryRepository payHistoryRepository;
    private final UserRepository userRepository;

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

        // PersonalUser
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);

        if(personalUser == null) {
            throw new PersonalNotFoundException("Personal User Not Found");
        }

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
            Payment approvedPayment = paymentService.approvePayment(savedPayment.getPayId(), amount);

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


    /**
     * 개인 유저의 총 베리 포인트를 반환
     *
     * @param userId 유저 아이디
     * @return ResponseEntity<Map<String, Integer>> 총 베리 포인트
     */
    @GetMapping("/personal/totalBerryPoints/{userId}")
    public ResponseEntity<Map<String, Integer>> getTotalBerryPoints(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.myTotalBerryPoints(userId));
    }


    /**
     * 포인트 사용 내역 리스트
     *
     * @param userId 유저 아이디
     * @return ResponseEntity<PersonalUserPointDTO> 남은 포인트 정보
     */
    @PostMapping("/personal/points/{userId}")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getPersonalUserRemainingPoints(@PathVariable Long userId) {
        User user = userRepository.findByUserId(userId);

        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
    

        Optional<List<Payment>> paymentsOptional = paymentRepository.findAllSuccessfulPaymentsByPersonalId(personalUser.getPersonalId());
        List<Map<String, String>> paymentDetailsList = new ArrayList<>();
        if (paymentsOptional.isEmpty() || paymentsOptional.get().isEmpty()) {
        } else {
            List<Payment> payments = paymentsOptional.get();
            paymentDetailsList = payments.stream().map(payment -> {
                Map<String, String> paymentDetails = new HashMap<>();
                paymentDetails.put("payId", payment.getPayId().toString());
                paymentDetails.put("payState", payment.getPayState().name());
                paymentDetails.put("amount", payment.getAmount().toString());
                paymentDetails.put("payDate", payment.getPayDate().toString());
                paymentDetails.put("paymentKey", payment.getPaymentKey());
                paymentDetails.put("berryPoint", payment.getBerryPoint().toString());
                paymentDetails.put("orderId", payment.getOrderId());
                return paymentDetails;
            }).collect(Collectors.toList());
        }

        List<PayHistory> payHistoryList = payHistoryRepository.findByPersonalUserPersonalId(personalUser.getPersonalId());
        List<Map<String, String>> payHistoryDetailsList = new ArrayList<>();
        if (payHistoryList.isEmpty()) {
        } else {
            payHistoryDetailsList = payHistoryList.stream().map(payHistory -> {
                Map<String, String> payHistoryDetails = new HashMap<>();
                payHistoryDetails.put("payHistoryId", payHistory.getPayHistoryId().toString());
                payHistoryDetails.put("sellerId", payHistory.getSellerId().toString());
                payHistoryDetails.put("buyerId", payHistory.getBuyerId().toString());
                payHistoryDetails.put("berryBucket", payHistory.getBerryBucket());
                payHistoryDetails.put("payHistoryContent", payHistory.getPayHistoryContent());
                payHistoryDetails.put("payHistoryDate", payHistory.getPayHistoryDate().toString());
                return payHistoryDetails;
            }).collect(Collectors.toList());
        }

        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("payment", paymentDetailsList);
        response.put("payHistory", payHistoryDetailsList);

        return ResponseEntity.ok(response);
    }

}
