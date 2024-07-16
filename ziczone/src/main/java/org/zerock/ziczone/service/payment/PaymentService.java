package org.zerock.ziczone.service.payment;

import java.util.Map;

public interface PaymentService {

    Map<String, Object> approvePayment(String paymentKey, String orderId, int amount);

}
