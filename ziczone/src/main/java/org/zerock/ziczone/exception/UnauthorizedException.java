package org.zerock.ziczone.exception;
// 인증 관련 예외를 처리하기 위한 클래스
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}