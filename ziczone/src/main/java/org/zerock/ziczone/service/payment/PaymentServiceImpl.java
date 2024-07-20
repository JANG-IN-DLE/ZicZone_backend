package org.zerock.ziczone.service.payment;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.payment.PayState;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.payment.PaymentDTO;
import org.zerock.ziczone.repository.payment.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;


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
}
