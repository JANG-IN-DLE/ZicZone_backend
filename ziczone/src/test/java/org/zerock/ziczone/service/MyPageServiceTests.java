package org.zerock.ziczone.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.ziczone.dto.mypage.CompanyUserDTO;
import org.zerock.ziczone.dto.mypage.PersonalUserDTO;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.application.ResumeRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.service.myPage.MyPageService;

import java.util.List;

@Slf4j
@SpringBootTest
@Transactional
public class MyPageServiceTests {

    @Autowired
    private MyPageService myPageService;
    @Autowired
    private PayHistoryRepository payHistoryRepository;
    @Autowired
    private PersonalUserRepository personalUserRepository;

    // 테스트용 유저 User 테이블 PK
    private final Long user_id = 1L;
    @Autowired
    private ResumeRepository resumeRepository;

    /**
     * @용도 : 기업회원 조회
     * @request : Long user_id
     * @response  : CompanyUserDTO
     */
    @Test
    public void getCompanyUserDTO() {
        CompanyUserDTO companyUserDTO = myPageService.getCompanyUserDTO(user_id);

        log.info("CompanyUserDTO: " + companyUserDTO);
    }

    /**
     * @용도 : 개인회원 조회
     * @request : Long user_id
     * @response  : PersonalUserDTO
     */
    @Test
    public void getPersonalUserDTO() {
        PersonalUserDTO personalUserDTO = myPageService.getPersonalUserDTO(user_id);
        log.info("PersonalUserDTO: " + personalUserDTO);
    }
    /**
     * @용도 : 개인회원들에게 지원서 공개 여부 (True만)
     * @request :
     * @response  : List<Long> user_id
     */
    @Test
    public void getVisiblePersonalIds() {
        List<Long> id =  myPageService.getVisiblePersonalIds();
        log.info("id: " + id);
    }

    /**
     * @용도 : 기업회원들에게 지원서 공개 여부 (True만)
     * @request :
     * @response  : List<Long> user_id
     */
    @Test
    public void getVisibleCompanyIds() {
        List<Long> id =  myPageService.getVisibleCompanyIds();
        log.info("id: " + id);
    }
    

}
