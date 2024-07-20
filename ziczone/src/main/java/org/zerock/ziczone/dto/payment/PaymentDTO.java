package org.zerock.ziczone.dto.payment;

import lombok.*;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.payment.PayState;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class PaymentDTO {

    private PayState payState;
    private Integer amount;
    private LocalDateTime payDate;
    private String paymentKey;
    private PersonalUser personalUser;
    private String orderId;


}
