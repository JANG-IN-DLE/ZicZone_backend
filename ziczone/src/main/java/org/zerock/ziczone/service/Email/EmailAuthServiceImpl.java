package org.zerock.ziczone.service.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.dto.Email.EmailAuth;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService {

    private final JavaMailSender mailSender; //메일을 보내기 위한 객체

    // <이메일, 인증코드> : 이메일을 key값으로 가짐
    private Map<String, EmailAuth> emailCodeMap = new HashMap<>();

    // 이메일 전송
    @Override
    public void sendVerificationEmail(String email){
        String authCode = generateAuthCode(); // 난수 생성
        emailCodeMap.put(email, new EmailAuth(email, authCode, LocalDateTime.now())); // 이메일, 난수, 생성 시간 삽입
        try {
            sendEmail(email, authCode);
        } catch (Exception e) {
            log.info("이메일 전송 실패");
        }
    }

    //코드건증함수
    @Override
    public boolean verifyEmailCode(String email, String code) {
        EmailAuth savedAuth = emailCodeMap.get(email); // 받은 이메일주소에 해당하는 EamilAuth객체
        if (savedAuth == null) { //만약 해당 객체에 코드가 없으면 false반환
            return false;
        }

        LocalDateTime now = LocalDateTime.now(); // 현재시간
        LocalDateTime createTime = savedAuth.getCreateTime(); // 객체에서 생성시간 가져오기

        // 유효 시간 3분 설정 : 생성시간에 3분을 더한 시간이 현재 시간보다 이전인지 확인
        if (createTime.plusMinutes(1).isBefore(now)) {
            emailCodeMap.remove(email); // 만료된 코드 제거
            log.info("인증번호 유효시간 지남");
            return false;
        }

        //저장된 인증 코드와 사용자가 입력한 인증코드가 일치하는지 확인(일치하면 true, 불일치함녀 false)
        return savedAuth.getCode().equals(code);
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

    // 난수 생성(8자리 대소문자, 숫자)
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