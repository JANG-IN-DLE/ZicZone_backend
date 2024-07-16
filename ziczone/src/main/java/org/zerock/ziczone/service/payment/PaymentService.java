package org.zerock.ziczone.service.payment;

import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public interface PaymentService {

    Map<String, Object> approvePayment(String paymentKey, String orderId, int amount) throws IOException;

    void processSuccessfulPayment(String paymentKey, String orderId, int amount);

    void processFailedPayment(String paymentKey, String orderId, int amount, JSONObject errorResponse);
}
