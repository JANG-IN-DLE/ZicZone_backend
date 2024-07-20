package org.zerock.ziczone.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.ziczone.domain.*;
import org.zerock.ziczone.domain.alarm.Alarm;
import org.zerock.ziczone.domain.alarm.AlarmContent;
import org.zerock.ziczone.domain.application.*;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.domain.board.Comment;
import org.zerock.ziczone.domain.job.Job;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.member.*;
import org.zerock.ziczone.domain.payment.PayState;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.domain.tech.Tech;
import org.zerock.ziczone.domain.tech.TechStack;
import org.zerock.ziczone.repository.alarm.AlarmContentRepository;
import org.zerock.ziczone.repository.alarm.AlarmRepository;
import org.zerock.ziczone.repository.application.*;
import org.zerock.ziczone.repository.board.BoardRepository;
import org.zerock.ziczone.repository.board.CommentRepository;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.job.JobRepository;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;
import org.zerock.ziczone.repository.tech.TechRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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
    private CommentRepository replyRepository;
    private JobRepository jobRepository;
    @Autowired
    private JobPositionRepository jobPositionRepository;
    @Autowired
    private TechRepository techRepository;
    @Autowired
    private TechStackRepository techStackRepository;
    @Autowired
    private PayHistoryRepository payHistoryRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AlarmContentRepository alarmContentRepository;
    @Autowired
    private AlarmRepository alarmRepository;

    @Test
    public void testPersonalInsert(){
        User user = User.builder()
                .userName("홍길동")
                .email("hong@gmail.com")
                .password("1234")
                .userIntro("홍길동입니다.")
                .userType(UserType.PERSONAL)
                .build();
        userRepository.save(user);

        PersonalUser personalUser = PersonalUser.builder()
                .personalCareer("신입")
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
                .resumeDate("1998년04월06일")
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
//                .corrPdf("테스트 pdf")
                .corrPoint(100)
                .corrCreate(LocalDateTime.now())
                .corrModify(LocalDateTime.now())
                .user(user)
                .build();

        boardRepository.save(board);
        log.info("Board saved: " + board);
    }

    @Test
    public void testBoardSelect() {
        Long corrId = 2L;

        Optional<Board> result = boardRepository.findById(corrId);

        Board board = result.orElseThrow();

        log.info("testBoardSelect : " + board);
    }

    @Test
    public void testBoardUpdate() {
        Long corrId = 2L;

        Optional<Board> result = boardRepository.findById(corrId);

        Board board = result.orElseThrow();

        board.change("테스트 제목 업데이트", "테스트 내용 업데이트", "update.pdf");

        boardRepository.save(board);
    }

    @Test
    public void testBoardDelete() {
        Long corrId = 3L;

        boardRepository.deleteById(corrId);
    }

    @Test
    public void testReplyInsert() {
        Long userId = 1L;
        User user = User.builder()
                .userId(userId)
                .build();

        Long corrId = 2L;
        Board board = Board.builder()
                .corrId(corrId)
                .build();

        Comment reply = Comment.builder()
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

    @Test
    public void testJobInsert(){
        Job job1 = Job.builder()
                .jobName("서버/백엔드")
                .build();
        jobRepository.save(job1);

        Job job2 = Job.builder()
                .jobName("프론트엔드")
                .build();
        jobRepository.save(job2);
        Job job3 = Job.builder()
                .jobName("프론트엔드")
                .build();
        jobRepository.save(job3);
        Job job4 = Job.builder()
                .jobName("안드로이드")
                .build();
        jobRepository.save(job4);
        Job job5 = Job.builder()
                .jobName("QA")
                .build();
        jobRepository.save(job5);
        Job job6 = Job.builder()
                .jobName("게임서버")
                .build();
        jobRepository.save(job6);
        Job job7 = Job.builder()
                .jobName("빅데이터")
                .build();
        jobRepository.save(job7);
        Job job8 = Job.builder()
                .jobName("IOS")
                .build();
        jobRepository.save(job8);
        Job job9 = Job.builder()
                .jobName("크로스플랫폼")
                .build();
        jobRepository.save(job9);
        Job job10 = Job.builder()
                .jobName("게임 클라이언트")
                .build();
        jobRepository.save(job10);
        Job job11 = Job.builder()
                .jobName("인공지능/머신러닝")
                .build();
        jobRepository.save(job11);
        Job job12 = Job.builder()
                .jobName("DBA")
                .build();
        jobRepository.save(job12);
        Job job13 = Job.builder()
                .jobName("devops/시스템")
                .build();
        jobRepository.save(job13);
        Job job14 = Job.builder()
                .jobName("정보보안")
                .build();
        jobRepository.save(job14);
        Job job15 = Job.builder()
                .jobName("블록체인")
                .build();
        jobRepository.save(job15);
        Job job16 = Job.builder()
                .jobName("개발PM")
                .build();
        jobRepository.save(job16);
        Job job17 = Job.builder()
                .jobName("기술지원")
                .build();
        jobRepository.save(job17);
        Job job18 = Job.builder()
                .jobName("HW/임베디드")
                .build();
        jobRepository.save(job18);
        Job job19 = Job.builder()
                .jobName("SW/솔루션")
                .build();
        jobRepository.save(job19);
        Job job20 = Job.builder()
                .jobName("웹퍼블리셔")
                .build();
        jobRepository.save(job20);

        log.info("Jobs saved: " + job1 + ", " + job2 + ", " + job3 + ", " + job4 + ", " + job5 + ", " + job6 + ", " + job7);
    }
    @Test
    public void testJobPositionInsert(){
        PersonalUser personalUser = personalUserRepository.findByPersonalId(1L);
        Job job1 = jobRepository.findByJobId(1L);
        Job job2 = jobRepository.findByJobId(2L);

        JobPosition jobPosition1 = JobPosition.builder()
                .personalUser(personalUser)
                .job(job1)
                .build();
        JobPosition jobPosition2 = JobPosition.builder()
                .personalUser(personalUser)
                .job(job2)
                .build();
        jobPositionRepository.save(jobPosition1);
        jobPositionRepository.save(jobPosition2);
        log.info("JobPosition saved: " + jobPosition1);
        log.info("JobPosition saved: " + jobPosition2);
    }
    @Test
    public void testTechInsert(){

        Tech tech1 = Tech.builder()
                .techName("JavaScript")
                .build();
        techRepository.save(tech1);
        Tech tech2 = Tech.builder()
                .techName("Python")
                .build();
        techRepository.save(tech2);
        Tech tech3 = Tech.builder()
                .techName("Java")
                .build();
        techRepository.save(tech3);
        log.info("Tech saved: " + tech1 + ", " + tech2 + ", " + tech3);
    }
    @Test
    public void testTechStackInsert(){
        PersonalUser personalUser = personalUserRepository.findByPersonalId(1L);
        Tech tech1 = techRepository.findByTechId(1L);
        Tech tech2 = techRepository.findByTechId(2L);

        TechStack techStack1 = TechStack.builder()
                .personalUser(personalUser)
                .tech(tech1)
                .build();
        techStackRepository.save(techStack1);
        TechStack techStack2 = TechStack.builder()
                .personalUser(personalUser)
                .tech(tech2)
                .build();
        techStackRepository.save(techStack2);
        log.info("TechStack saved: " + techStack1 + "," + techStack2);
    }
    @Test
    public void testAppPaymentInsert(){
        PersonalUser personalUser = personalUserRepository.findByPersonalId(1L);

        PayHistory payHistory = PayHistory.builder()
                .sellerId(4L)
                .berryBucket("-50")
                .personalUser(personalUser)
                .build();
        payHistoryRepository.save(payHistory);
        log.info("AppPayment saved: " + payHistory);
    }
    @Test
    public void testPaymentInsert(){
        PersonalUser personalUser = personalUserRepository.findByPersonalId(1L);

        Payment payment = Payment.builder()
                .payState(PayState.SUCCESS)
                .berryPoint(100)
                .personalUser(personalUser)
                .build();
        paymentRepository.save(payment);
        log.info("Payment saved: " + payment);
    }
    @Test
    public void testAlarmContentInsert(){
        AlarmContent alarmContent = AlarmContent.builder()
                .alarmType("컨택")
                .build();
        alarmContentRepository.save(alarmContent);
        log.info("AlarmContent saved: " + alarmContent);

    }
    @Test
    public void testAlarmInsert(){
        AlarmContent alarmContent = alarmContentRepository.findByAlarmContentId(1L);
        User user = userRepository.findByUserId(2L);

        Alarm alarm = Alarm.builder()
                .readOrNot(false)
                .user(user)
                .alarmContent(alarmContent)
                .build();
        alarmRepository.save(alarm);
        log.info("Alarm saved: " + alarm);
    }
}
