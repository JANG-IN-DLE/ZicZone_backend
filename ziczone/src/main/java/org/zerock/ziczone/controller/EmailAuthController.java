package org.zerock.ziczone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.service.EmailAuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class EmailAuthController {

    @Autowired
    private EmailAuthService emailAuthService;

    // 이메일 전송
    @PostMapping("/email-verification")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailAuthService.sendVerificationEmail(email);
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    // 인증코드 검증
    @PostMapping("/email-verification/complete")
    public ResponseEntity<String> verifyEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean isVerified = emailAuthService.verifyEmailCode(email, code);
        if (isVerified) {
            return ResponseEntity.ok("Auth Success");
        } else {
            return ResponseEntity.ok("Auth Fail");
        }
    }
}