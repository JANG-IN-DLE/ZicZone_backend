package org.zerock.ziczone.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.config.PayConfig;

import java.net.http.HttpClient;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final PayConfig payConfig;


    @Override
    public Map<String, Object> approvePayment(String paymentKey, String orderId, int amount) {
        return Map.of();
    }
}
