package org.zerock.ziczone.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.ziczone.domain.member.*;
import org.zerock.ziczone.dto.mypage.*;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.application.*;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.service.myPage.MyPageService;
import org.zerock.ziczone.service.myPage.ResumeServiceImpl;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class MyPageServiceTests {

    @Autowired
    private MyPageService myPageService;
    @Autowired
    private PayHistoryRepository payHistoryRepository;
    @Autowired
    private ResumeServiceImpl resumeService;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ArchiveRepository archiveRepository;

    @Autowired
    private EtcRepository etcRepository;

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyUserRepository companyUserRepository;
    @Autowired
    private PersonalUserRepository personalUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private final Long user_id = 231212L;
    private User testUser;
    private PersonalUser testPersonalUser;
    private CompanyUser testCompanyUser;

    @BeforeEach
    void setUp() {
        String encryptedPassword = passwordEncoder.encode("password");
        testUser = User.builder()
                .email("testuser@example.com")
                .password(passwordEncoder.encode("password"))
                .userName("Test User")
                .userIntro("Intro")
                .userType(UserType.PERSONAL)
                .build();

        userRepository.save(testUser);

        testPersonalUser = PersonalUser.builder()
                .personalCareer("신입")
                .isPersonalVisible(true)
                .isCompanyVisible(false)
                .gender(Gender.MALE)
                .user(testUser)
                .build();

        personalUserRepository.save(testPersonalUser);
        // User 엔티티와 PersonalUser 엔티티 간의 관계 설정
        testUser.toBuilder().personalUser(testPersonalUser).build();
        userRepository.save(testUser);
        // 기본 Resume 생성
        resumeService.getResume(testUser.getUserId());


        testCompanyUser = CompanyUser.builder()
                .user(testUser)
                .companyAddr("Test Address")
                .companyLogo("test_logo.png")
                .companyCeo("Test CEO")
                .companyNum("12345")
                .companyYear(LocalDate.now())
                .build();
        companyUserRepository.saveAndFlush(testCompanyUser);
    }

    // 기업 회원 조회
    @Test
    void testGetCompanyUserDTO() {
        CompanyUserDTO companyUserDTO = myPageService.getCompanyUserDTO(testUser.getUserId());
        assertThat(companyUserDTO).isNotNull();
        assertThat(companyUserDTO.getCompanyAddr()).isEqualTo(testCompanyUser.getCompanyAddr());
        log.info("CompanyUserDTO: " + companyUserDTO);
    }
    // 기업 회원 정보 수정
    @Test
    void testUpdateCompanyUser() {
        CompanyUserUpdateDTO updateDTO = CompanyUserUpdateDTO.builder()
                .currentPassword("password")
                .changePassword("NewPassword123!")
                .companyAddr("New Address")
                .companyLogo("new_logo.png")
                .userName("Updated User")
                .userIntro("Updated Introduction")
                .build();

        String result = myPageService.updateCompanyUser(testUser.getUserId(), updateDTO);
        assertThat(result).isEqualTo("User Information Updated Successfully");

        CompanyUserDTO updatedCompanyUserDTO = myPageService.getCompanyUserDTO(testUser.getUserId());
        assertThat(updatedCompanyUserDTO.getCompanyAddr()).isEqualTo("New Address");
        assertThat(updatedCompanyUserDTO.getUser().getUserName()).isEqualTo("Updated User");
        assertThat(updatedCompanyUserDTO.getUser().getUserIntro()).isEqualTo("Updated Introduction");
    }
    // 개인 회원 조회
    @Test
    void testGetPersonalUserDTO() {
        PersonalUserDTO personalUserDTO = myPageService.getPersonalUserDTO(testUser.getUserId());
        assertThat(personalUserDTO).isNotNull();
        assertThat(personalUserDTO.getPersonalCareer()).isEqualTo(testPersonalUser.getPersonalCareer());
        log.info("PersonalUserDTO: " + personalUserDTO);
    }

    // 테스트시 실제 저장된 유저데이터로 조회
    @Test
    void testGetAggregatedData() {
        AggregatedDataDTO aggregatedDataDTO = myPageService.getAggregatedData(2L);
        assertThat(aggregatedDataDTO).isNotNull();
        assertThat(aggregatedDataDTO.getPersonalUsers()).isNotNull();
        log.info("AggregatedDataDTO: " + aggregatedDataDTO);
    }

    // 테스트 시 실제 저장된 데이터로 조회 할 것
    @Test
    void testGetPicksByCompanyUsers() {
        List<PersonalUserDTO> picks = myPageService.getPicksByCompanyUsers(1L);
        assertThat(picks).isNotNull();
        log.info("Picks by Company Users: " + picks);
    }


    @Test
    void testGetPicksByPersonalUsers() {
        List<CompanyUserDTO> picks = myPageService.getPicksByPersonalUsers(2L);
        assertThat(picks).isNotNull();
        log.info("Picks by Personal Users: " + picks);
    }

    @Test
    void testMyCommList() {
        List<MyCommentListDTO> commentList = myPageService.MyCommList(2L);
        assertThat(commentList).isNotNull();
        log.info("My Comment List: " + commentList);
    }




}
