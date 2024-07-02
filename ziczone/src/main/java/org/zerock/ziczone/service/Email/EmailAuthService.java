package org.zerock.ziczone.service.Email;

public interface EmailAuthService {
    void sendVerificationEmail(String email);
    boolean verifyEmailCode(String email, String code);
}