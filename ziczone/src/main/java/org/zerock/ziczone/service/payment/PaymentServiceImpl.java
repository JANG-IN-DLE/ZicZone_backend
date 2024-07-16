package org.zerock.ziczone.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.ziczone.config.PayConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PayConfig payConfig;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Map<String, Object> approvePayment(String paymentKey, String orderId, int amount) throws IOException {
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");

        String clientApiKey = payConfig.getTestClientApiKey();
        String secretApiKey = payConfig.getTestSecretApiKey();

        // Request Headers
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretApiKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic" + new String(encodedBytes);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(orderId.getBytes(StandardCharsets.UTF_8));
        outputStream.write(paymentKey.getBytes(StandardCharsets.UTF_8));
        outputStream.write(Integer.toString(amount).getBytes(StandardCharsets.UTF_8));

        // Response
        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

        Map responseBody = objectMapper.readValue(reader, Map.class);
        responseStream.close();
        connection.disconnect();




        return responseBody;
    }

    @Override
    public void processSuccessfulPayment(String paymentKey, String orderId, int amount) {
        // 결제 성공 시 비즈니스 로직을 구현합니다.
        // 예: 데이터베이스 업데이트, 사용자 알림 전송 등
        System.out.println("결제 성공:");
        System.out.println("Payment Key: " + paymentKey);
        System.out.println("Order ID: " + orderId);
        System.out.println("Amount: " + amount);
    }

    @Override
    public void processFailedPayment(String paymentKey, String orderId, int amount, JSONObject errorResponse) {
        // 결제 실패 시 비즈니스 로직을 구현합니다.
        // 예: 데이터베이스 업데이트, 관리자 알림 전송 등
        System.out.println("결제 실패:");
        System.out.println("Payment Key: " + paymentKey);
        System.out.println("Order ID: " + orderId);
        System.out.println("Amount: " + amount);
        System.out.println("Error Response: " + errorResponse);
    }
}
