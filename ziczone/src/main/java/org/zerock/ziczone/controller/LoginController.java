package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.join.PersonalUserDTO;
import org.zerock.ziczone.service.Email.EmailAuthService;
import org.zerock.ziczone.service.join.JoinService;
import org.zerock.ziczone.service.login.LoginService;

import java.util.Map;
import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final EmailAuthService emailAuthService;
    private final JoinService joinService;
    private final LoginService loginService;

    //비밀번호 찾기(메일인증)
    @PostMapping("/emailAuth")
    public ResponseEntity<String> sendAuthEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User EmailDuplication = joinService.EmailDuplication(email);
        if(EmailDuplication != null) {
            emailAuthService.sendVerificationEmail(email);
            return ResponseEntity.ok("email sent");
        }else {
            return ResponseEntity.ok("email empty");
        }
    }

    // 인증코드 검증
    @PostMapping("/emailAuth/verify-email")
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

    //비밀번호 변경
    @PostMapping("/emailAuth/change-password")
    public ResponseEntity<String> changeEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        User user = joinService.EmailDuplication(email);
        String result = loginService.changePassword(user, password);
        if(result != null) {
            return ResponseEntity.ok("change Password Success");
        }else {
            return ResponseEntity.ok("change Password Fail");
        }
    }
}
