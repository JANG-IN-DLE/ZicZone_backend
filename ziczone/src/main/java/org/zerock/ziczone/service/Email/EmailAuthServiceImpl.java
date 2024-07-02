package org.zerock.ziczone.service.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService {

    private final JavaMailSender mailSender; //메일을 보내기 위한 객체

    // <이메일, 인증코드> : 이메일을 key값으로 가짐
    private Map<String, String> emailCodeMap = new HashMap<>();

    // 이메일 전송 함수
    @Override
    public void sendVerificationEmail(String email) {
        String authCode = generateAuthCode(); //난수생성
        emailCodeMap.put(email, authCode); //이메일, 난수 삽입
        try {
            sendEmail(email, authCode);
        } catch (MessagingException e) {
            e.printStackTrace();
            log.info("이메일 전송 실패");
        }
    }

    // 코드 검증 함수
    @Override
    public boolean verifyEmailCode(String email, String code) {
        String savedCode = emailCodeMap.get(email);
        return savedCode != null && savedCode.equals(code);
    }

    // 실제로 이메일을 전송하는 코드
    private void sendEmail(String email, String authCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage(); //이메일 메시지객체를 생성
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // 이메일메시지를 쉽게 작성할 수 있도록 도와주는 헬퍼(true : 멀티파트 메시지를 허용, 첨부파일)

        helper.setTo(email); //수신자
        helper.setSubject("이메일 인증 코드"); //제목
        helper.setText("인증 코드는 " + authCode + " 입니다.", true); //내용

        mailSender.send(message); //전송
    }

    // 난수 생성
    private String generateAuthCode() {
        int length = 8; // 난수의 길이
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder authCode = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            authCode.append(characters.charAt(random.nextInt(characters.length())));
        }

        log.info("@@@@@@@@@@authCode: " + authCode.toString()); // 확인log
        return authCode.toString();
    }
}