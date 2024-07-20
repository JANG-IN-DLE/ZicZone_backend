package org.zerock.ziczone.service.payment;

import java.io.IOException;
import java.util.Map;

public interface TossPayService {

    Map<String, Object> confirmPayment(String orderId, String paymentKey, int amount) throws IOException;

}
