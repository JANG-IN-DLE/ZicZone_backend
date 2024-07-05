package org.zerock.ziczone.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.ziczone.domain.member.Gender;
import org.zerock.ziczone.domain.member.UserType;
import org.zerock.ziczone.dto.join.PersonalUserDTO;
import org.zerock.ziczone.service.join.JoinServiceImpl;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Log4j2
public class JoinServiceTests {
    @Autowired
    private JoinServiceImpl joinService;

    //개인회원가입 테스트
    @Test
    public void testPersonalJoin() {
        PersonalUserDTO personalUserDTO = PersonalUserDTO.builder()
                .userName("testUser")
                .email("test@example.com")
                .password("password")
                .userIntro("intro")
                .userType(UserType.PERSONAL)
                .personalCareer("career")
                .gender(Gender.MALE)
                .jobIds(Arrays.asList(1L, 2L))
                .techIds(Arrays.asList(1L, 2L))
                .build();

        String result = joinService.personalJoin(personalUserDTO);
        assertEquals("signUp success", result);

        log.info("회원 가입 결과: " + result);
    }
}