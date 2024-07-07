package org.zerock.ziczone.service.Email;

import org.zerock.ziczone.domain.member.User;

public interface EmailAuthService {
    //이메일 보내는 함수
    void sendVerificationEmail(String email);

    //인증코드 검증
    boolean verifyEmailCode(String email, String code);

}