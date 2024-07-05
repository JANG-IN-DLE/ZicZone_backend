package org.zerock.ziczone.service.Email;

public interface EmailAuthService {
    //이메일 보내는 함수
    void sendVerificationEmail(String email);

    //인증코드 검증
    boolean verifyEmailCode(String email, String code);

    //이메일 중복검사
    boolean EmailDuplication(String email);
}