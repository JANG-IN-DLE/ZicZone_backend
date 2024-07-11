package org.zerock.ziczone.dto.payment;

import lombok.NonNull;

public class PaymentDTO {


//    @NonNull
//    private PayType payType;

    @NonNull
    private Long amount;

    @NonNull
    private String orderName;

    private String yourSucceesUrl;

    private String yourFaulUrl;

}
