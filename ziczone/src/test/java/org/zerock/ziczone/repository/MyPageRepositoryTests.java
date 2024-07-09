package org.zerock.ziczone.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.ziczone.domain.member.*;
import org.zerock.ziczone.repository.application.ResumeRepository;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;

import java.time.LocalDate;

@SpringBootTest
@Transactional
@Log4j2
public class MyPageRepositoryTests {


    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  CompanyUserRepository companyUserRepository;
    @Autowired
    private  PersonalUserRepository personalUserRepository;
    @Autowired
    private  PaymentRepository paymentRepository;
    @Autowired
    private  ResumeRepository resumeRepository;
    @Autowired
    private  PickAndScrapRepository pickAndScrapRepository;



    /**
     * @용도 : 기업회원 마이페이지 수정
     * @request :  user_id
     * @response  : List<Long> Update UserDTO(개인/기업)
     */
    @Test
    void getCompanyUserDTO_Myinfo() {
        //given
        User user = User.builder()
                .email("user1@test.com")
                .password("1234")
                .userName("kk")
                .userType(UserType.COMPANY)
                .userIntro("k")
                .build();
        userRepository.save(user);

        CompanyUser companyUser = CompanyUser.builder()
                .companyNum("12345")
                .companyAddr("123 Street")
                .companyYear(LocalDate.of(2024, 1, 1))
                .companyLogo("logo.png")
                .companyCeo("CEO Name")
                .user(user)
                .build();
        companyUserRepository.save(companyUser);

        log.info("CompanyUser saved: " + companyUser);

        // update
        CompanyUser updatedCompanyUser = CompanyUser.builder()
                .companyId(companyUser.getCompanyId()) // existing ID to ensure update
                .companyNum(companyUser.getCompanyNum())
                .companyAddr("Updated Address")
                .companyYear(companyUser.getCompanyYear())
                .companyLogo(companyUser.getCompanyLogo())
                .companyCeo("Updated CEO Name")
                .user(companyUser.getUser())
                .build();
        companyUserRepository.save(updatedCompanyUser);

        User UpdatedUser = User.builder()
                .email(user.getEmail())
                .password("Update_1234")
                .userName(user.getUserName())
                .userType(user.getUserType())
                .userIntro("Update_User_Intro")
                .build();
        userRepository.save(UpdatedUser);

        // verify update
        CompanyUser cresult = companyUserRepository.findById(companyUser.getCompanyId()).orElseThrow(() -> new RuntimeException("CompanyUser not found"));
        User presult = userRepository.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("CompanyUser not found"));
        log.info("CompanyUser updated: " + cresult);
        log.info("PersonalUser updated: " + presult);
    }


    /**
     * @용도 : 개인회원 마이페이지 수정
     * @request :  user_id
     * @response  : List<Long> Update UserDTO(개인/기업)
     */
    @Test
    void getPersonalUserDTO_Myinfo() {
        //given
        User user = User.builder()
                .email("personal@example.com")
                .password("password")
                .userName("Personal User")
                .userIntro("Personal introduction")
                .userType(UserType.PERSONAL)
                .build();
        userRepository.save(user);

        PersonalUser personalUser = PersonalUser.builder()
                .personalCareer("신입")
                .isPersonalVisible(true)
                .isCompanyVisible(false)
                .gender(Gender.FEMALE)
                .user(user)
                .build();
        personalUserRepository.save(personalUser);

        log.info("user saved: " + user);
        log.info("PersonalUser saved: " + personalUser);

        // update
        PersonalUser updatedPersonalUser = PersonalUser.builder()
                .personalCareer("Update_경력")
                .isPersonalVisible(false)
                .isCompanyVisible(true)
                .gender(personalUser.getGender())
                .user(user)
                .build();
        personalUserRepository.save(updatedPersonalUser);

        User UpdatedUser = User.builder()
                .email(user.getEmail())
                .password("Update_1234")
                .userName(user.getUserName())
                .userType(user.getUserType())
                .userIntro("Update_User_Intro")
                .build();
        userRepository.save(UpdatedUser);

        log.info("Update UpdatedUser saved: " + UpdatedUser);
        log.info("Update updatedPersonalUser saved: " + updatedPersonalUser);

    }

}
