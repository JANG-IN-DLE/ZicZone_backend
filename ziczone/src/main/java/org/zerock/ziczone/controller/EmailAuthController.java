package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.service.Email.EmailAuthService;
import org.zerock.ziczone.service.join.JoinService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class EmailAuthController {

    private final EmailAuthService emailAuthService;
    private final JoinService joinService;

    // 이메일 전송
    @PostMapping("/email-verification")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User EmailDuplication = joinService.EmailDuplication(email);
        if(EmailDuplication != null) {
            return ResponseEntity.ok("Email Duplication");
        }else {
            emailAuthService.sendVerificationEmail(email);
            return ResponseEntity.ok("email sent");
        }
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