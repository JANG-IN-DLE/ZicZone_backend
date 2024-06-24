package org.zerock.ziczone.repository;

import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.ziczone.domain.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@Log4j2
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonalUserRepository personalUserRepository;
    @Autowired
    private CompanyUserRepository companyUserRepository;
    @Autowired
    private PickAndScrapRepository pickAndScrapRepository;
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private EducationRepository educationRepository;
    @Autowired
    private ArchiveRepository archiveRepository;
    @Autowired
    private CareerRepository careerRepository;
    @Autowired
    private CurriculumRepository curriculumRepository;
    @Autowired
    private EtcRepository etcRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testPersonalInsert(){
        User user = User.builder()
                .userName("전민재")
                .email("alswo9672@gmail.com")
                .password("1234")
                .userIntro("전민재입니다.")
                .userType(UserType.PERSONAL)
                .build();
        userRepository.save(user);

        PersonalUser personalUser = PersonalUser.builder()
                .career("신입")
                .isPersonalVisible(true)
                .isCompanyVisible(true)
                .gender(Gender.MALE)
                .user(user)
                .build();
        personalUserRepository.save(personalUser);

        log.info("User saved: " + user);
        log.info("PersonalUser saved: " + personalUser);
    }

    @Test
    public void testCompanyInsert(){
        User user = User.builder()
                .userName("토스")
                .email("support@toss.im")
                .password("1234")
                .userIntro("toss입니다.")
                .userType(UserType.COMPANY)
                .build();
        userRepository.save(user);

        LocalDate companyYear = LocalDate.of(2013,8,1);

        CompanyUser companyUser = CompanyUser.builder()
                .companyNum("226-27-20508")
                .companyAddr("서울특별시 강남구 테헤란로 131")
                .companyYear(companyYear)
                .companyLogo("http://toss.png")
                .companyCeo("이승건")
                .user(user)
                .build();
        companyUserRepository.save(companyUser);

        log.info("User saved: " + user);
        log.info("CompanyUser saved: " + companyUser);
    }

    @Test
    public void testPickAndScrapInsert(){
        CompanyUser companyUser = companyUserRepository.findByCompanyId(1L);
        PersonalUser personalUser = personalUserRepository.findByPersonalId(1L);
        PickAndScrap pickAndScrap = PickAndScrap.builder()
                .pick(true)
                .scrap(true)
                .companyUser(companyUser)
                .personalUser(personalUser)
                .build();
        pickAndScrapRepository.save(pickAndScrap);

        log.info("PickAndScrap saved: " + pickAndScrap);
    }
    @Test
    public void testResumeInsert(){
        PersonalUser personalUser = personalUserRepository.findByPersonalId(1L);

        Resume resume = Resume.builder()
                .resumeName("전민재")
                .date("1998년04월06일")
                .phoneNum("010-2427-9672")
                .resumePhoto("http://photo.png")
                .personalState("http://personalState.png")
                .personalUser(personalUser)
                .build();

        resumeRepository.save(resume);
        log.info("Resume saved: " + resume);
    }
    @Test
    public void testPortfolioInsert(){
        Resume resume = resumeRepository.findByResumeId(1L);

        Portfolio portfolio = Portfolio.builder()
                .portFile("http://portfoliofile.png")
                .resume(resume)
                .build();
        portfolioRepository.save(portfolio);
        log.info("Portfolio saved: " + portfolio);

    }
    @Test
    public void testCertificateInsert(){
        Resume resume = resumeRepository.findByResumeId(1L);

        Certificate certificate = Certificate.builder()
                .cert("정보처리기사")
                .certDate("2024-06-23")
                .resume(resume)
                .build();
        certificateRepository.save(certificate);

        log.info("Certificate saved: " + certificate);

    }
    @Test
    public void testEduInsert(){
        Resume resume = resumeRepository.findByResumeId(1L);

        Education education = Education.builder()
                .edu("비트대학교")
                .credit("4.1/4.5")
                .eduDate("2024.07.30")
                .resume(resume)
                .build();
        educationRepository.save(education);
        log.info("Education saved: " + education);
    }
    @Test
    public void testArchInsert(){
        Resume resume = resumeRepository.findByResumeId(1L);

        Archive archive = Archive.builder()
                .archGit("https://github.com")
                .archNotion("https://notion.com")
                .archBlog("https://velog.com")
                .resume(resume)
                .build();
        archiveRepository.save(archive);
        log.info("Archive saved: " + archive);

    }
    @Test
    public void testCareerInsert(){
        Resume resume = resumeRepository.findByResumeId(1L);

        Career career = Career.builder()
                .careerName("배달의민족")
                .careerJob("frontend")
                .careerPosition("인턴")
                .careerDate("2023.07~2023.09")
                .resume(resume)
                .build();
        careerRepository.save(career);
        log.info("Career saved: " + career);

    }
    @Test
    public void testCurriInsert(){
        Resume resume = resumeRepository.findByResumeId(1L);

        Curriculum curriculum = Curriculum.builder()
                .curriContent("네이버클라우드 데브옵스 과정")
                .curriCompany("비트캠프")
                .curriDate("2024.01~2024.07")
                .resume(resume)
                .build();
        curriculumRepository.save(curriculum);

        log.info("Curriculum saved: " + curriculum);
    }
    @Test
    public void testEtcInsert(){
        Resume resume = resumeRepository.findByResumeId(1L);

        Etc etc = Etc.builder()
                .etcContent("캡스톤디자인우수상")
                .etcDate("2024.01.08")
                .resume(resume)
                .build();
        etcRepository.save(etc);
        log.info("Etc saved: " + etc);

    }

    @Test
    public void testBoardInsert(){
        // DB에 있는 user_id
        Long userId = 1L;
        User user = User.builder()
                .userId(userId)
                .build();

        Board board = Board.builder()
                .corrTitle("테스트 제목")
                .corrContent("테스트 내용")
                .corrPdf("테스트 pdf")
                .corrPoint(100)
                .corrCreate(LocalDateTime.now())
                .corrModify(LocalDateTime.now())
                .user(user)
                .build();

        boardRepository.save(board);
        log.info("Board saved: " + board);
    }

    @Test
    public void testReplyInsert(){
        Long userId = 1L;
        User user = User.builder()
                .userId(userId)
                .build();

        Long corrId = 2L;
        Board board = Board.builder()
                .corrId(corrId)
                .build();

        Reply reply = Reply.builder()
                .commContent("테스트 내용")
                .commSelection(false)
                .commCreate(LocalDateTime.now())
                .commModify(LocalDateTime.now())
                .user(user)
                .board(board)
                .build();

        replyRepository.save(reply);
        log.info("Reply saved: " + reply);
    }
}
