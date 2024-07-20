package org.zerock.ziczone.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.config.PayConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPayServiceImpl implements TossPayService{

    private final PayConfig payConfig;
    private final ObjectMapper objectMapper;


    @Override
    public Map<String, Object> confirmPayment(String orderId, String paymentKey, int amount) throws IOException {
        // 결제 요청 후 승인 요청 주소
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");

        String secreatApiKey = payConfig.getTestSecretApiKey();

        // Request Headers
//        Base64.Encoder encoder = Base64.getEncoder();
//        byte[] encodedBytes = encoder.encode((secreatApiKey + ":").getBytes(StandardCharsets.UTF_8));
//        String authorizations = "Basic " + new String(encodedBytes);
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);



        // 연결
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Json 바디 생성
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "orderId",orderId,
                "paymentKey",paymentKey,
                "amount",amount
        ));

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            log.info("outputStream : {}",outputStream);
        }


        // Response
        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        log.info("code : {}",code);


        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
        log.info("responseStream : {}",responseStream);
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        log.info("reader : {}",reader);

        Map<String, Object> responseBody = objectMapper.readValue(reader, Map.class);
        responseStream.close();

        if (!isSuccess){
            throw new IOException("Payment confirmation failed : "+responseBody.get("message"));
        }
        // 연결 종료
        connection.disconnect();
        return responseBody;
    }
}
