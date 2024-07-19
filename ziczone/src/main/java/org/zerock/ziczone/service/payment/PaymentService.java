package org.zerock.ziczone.service.payment;

import net.minidev.json.JSONObject;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.payment.PaymentDTO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public interface PaymentService {
    Payment getPayment(Long payId);

    Payment savePayment(PaymentDTO paymentDTO);

    Payment approvePayment(Long payId, int berryPoint);

    Payment failPayment(Long payId);
}
