package org.zerock.ziczone.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.payment.PayState;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.payment.PaymentDTO;
import org.zerock.ziczone.exception.mypage.PersonalNotFoundException;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayHistoryRepository payHistoryRepository;
    private final PersonalUserRepository personalUserRepository;


    @Override
    public Payment savePayment(PaymentDTO paymentDTO){
        Payment payment = Payment.builder()
                .orderId(paymentDTO.getOrderId())
                .amount(paymentDTO.getAmount())
                .berryPoint(0)
                .payDate(paymentDTO.getPayDate())
                .payState(PayState.PENDING)
                .paymentKey(paymentDTO.getPaymentKey())
                .personalUser(paymentDTO.getPersonalUser())
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    public Payment approvePayment(Long payId, int berryPoint) {
        Payment payment = getPayment(payId);
        payment = payment.toBuilder()
                .payState(PayState.SUCCESS)
                .berryPoint(berryPoint)
                .build();
        return paymentRepository.save(payment);

    }

    @Override
    public Payment getPayment(Long payId){
        return paymentRepository.findById(payId).orElseThrow(() -> new IllegalArgumentException("invalid Payment ID"));
    }


    @Override
    public Payment failPayment(Long payId) {
        Payment payment = getPayment(payId);
        Payment updatePayment = payment.toBuilder()
                .payState(PayState.FAILED)
                .build();
        return paymentRepository.save(updatePayment);
    }

    @Override
    public Map<String, Integer> myTotalBerryPoints(Long userId) {
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);

        if (personalUser == null) {
            throw new PersonalNotFoundException("personal not found");
        }

        // personalId로 성공한 결제들의 베리 포인트 합산
        Integer totalBerryPoints = paymentRepository.findTotalBerryPointsByPersonalId(personalUser.getPersonalId())
                .orElse(0); // Optional이 비어있는 경우 0을 기본값으로 반환

        // personalId로 PayHistory에서 berryBucket 값을 가져와 합산
        List<PayHistory> payHistoryList = payHistoryRepository.findByPersonalUserPersonalId(personalUser.getPersonalId());
        if (payHistoryList.isEmpty()) {

        }
        int totalBerryBucket = payHistoryList.stream()
                .mapToInt(payHistory -> Integer.parseInt(payHistory.getBerryBucket()))
                .sum();

        // 최종 총 베리 포인트 계산
        log.info("payHistoryList : {}", payHistoryList);
        log.info("totalBerryBucket : {}", totalBerryBucket);
        log.info("totalBerryPoints : {}", totalBerryPoints);
        int finalTotalBerryPoints = totalBerryPoints + (totalBerryBucket);
        log.info("finalTotalBerryPoints : {}", finalTotalBerryPoints);

        // 결과를 맵에 담아 반환
        Map<String, Integer> response = new HashMap<>();
        response.put("totalBerryPoints", finalTotalBerryPoints);

        return response;
    }


}
