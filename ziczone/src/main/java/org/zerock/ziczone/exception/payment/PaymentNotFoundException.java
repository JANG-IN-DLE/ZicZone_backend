package org.zerock.ziczone.exception.payment;
// 결제와 관련된 예외를 처리하기 위한 클래스
public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}