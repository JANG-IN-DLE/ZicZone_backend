package org.zerock.ziczone.service.pick;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.application.Career;
import org.zerock.ziczone.domain.application.Education;
import org.zerock.ziczone.domain.application.Resume;
import org.zerock.ziczone.domain.job.Job;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.pick.OpenCardDTO;
import org.zerock.ziczone.dto.pick.PickCardDTO;
import org.zerock.ziczone.dto.pick.PickJobDTO;
import org.zerock.ziczone.dto.pick.PickResumeDTO;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.application.*;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.job.JobRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PickServiceImpl implements PickService {

    private final PersonalUserRepository personalUserRepository;
    private final TechStackRepository techStackRepository;
    private final JobPositionRepository jobPositionRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final EducationRepository educationRepository;
    private final CareerRepository careerRepository;
    private final CertificateRepository certificateRepository;
    private final CurriculumRepository curriculumRepository;
    private final EtcRepository etcRepository;
    private final ArchiveRepository archiveRepository;
    private final PortfolioRepository portfolioRepository;
    private final PaymentRepository paymentRepository;
    private final PayHistoryRepository payHistoryRepository;

    //    pickzone 회원 card data 가져오는 메서드
    @Override
    public List<PickCardDTO> getPickCards() {
        List<PersonalUser> users = personalUserRepository.findAll();

        return users.stream().map(user -> {
            List<String> techNames = techStackRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(techStack -> techStack.getTech().getTechName())
                    .collect(Collectors.toList());
            List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(jobPosition -> jobPosition.getJob().getJobName())
                    .collect(Collectors.toList());
            return PickCardDTO.builder()
                    .userId(user.getUser().getUserId())
                    .personalId(user.getPersonalId())
                    .userName(user.getUser().getUserName())
                    .userIntro(user.getUser().getUserIntro())
                    .gender(user.getGender())
                    .personalCareer(user.getPersonalCareer())
                    .techName(String.join(",", techNames))
                    .jobName(String.join(",", jobNames))
                    .build();
        }).collect(Collectors.toList());

    }
//  pickzone Job 데이터 가져오는 메서드
    @Override
    public List<PickJobDTO> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .map(job -> PickJobDTO.builder()
                        .jobId(job.getJobId())
                        .jobName(job.getJobName())
                        .build())
                .collect(Collectors.toList());

    }
//  pickDetailzone 회원정보 가져오는 메서드
    @Override
    public PickCardDTO getPickCardsById(Long personalId) {
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);

        if(personalUser == null) {
            throw new RuntimeException("personal user not found");
        }
        List<String> techNames = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechName())
                .collect(Collectors.toList());
        List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(jobPosition -> jobPosition.getJob().getJobName())
                .collect(Collectors.toList());

        PickCardDTO pickCardDTO = PickCardDTO.builder()
                .userId(personalUser.getUser().getUserId())
                .personalId(personalUser.getPersonalId())
                .userName(personalUser.getUser().getUserName())
                .userIntro(personalUser.getUser().getUserIntro())
                .gender(personalUser.getGender())
                .personalCareer(personalUser.getPersonalCareer())
                .techName(String.join(",", techNames))
                .jobName(String.join(",", jobNames))
                .build();

        return pickCardDTO;
    }
//  pickDetailzone resume data GET요청 메서드
    @Override
    public PickResumeDTO getResumeById(Long personalId) {
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);
        if(personalUser == null) {
            throw new RuntimeException("personal user not found");
        }
        Resume resume = resumeRepository.findByPersonalUser_PersonalId(personalId);
        if(resume == null)  {
            throw new RuntimeException("resume not found");
        }
        List<String> techNames = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechName())
                .collect(Collectors.toList());
        List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(jobPosition -> jobPosition.getJob().getJobName())
                .collect(Collectors.toList());
        List<String> educations = educationRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(education -> education.getEdu() + "," + education.getCredit() + "," + education.getEduDate())
                .collect(Collectors.toList());
        List<String> careers = careerRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(career -> career.getCareerName()+ "," +career.getCareerJob() + "," + career.getCareerPosition() + ","+ career.getCareerDate())
                .collect(Collectors.toList());
        List<String> certificates = certificateRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(certificate -> certificate.getCert()+"," + certificate.getCertDate())
                .collect(Collectors.toList());
        List<String> curriculums = curriculumRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(curriculum -> curriculum.getCurriCompany()+","+curriculum.getCurriContent()+","+curriculum.getCurriDate())
                .collect(Collectors.toList());
        List<String> etcs = etcRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(etc -> etc.getEtcContent()+","+etc.getEtcDate())
                .collect(Collectors.toList());
        List<String> archives = archiveRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(archive -> archive.getArchBlog()+","+archive.getArchGit()+","+archive.getArchNotion())
                .collect(Collectors.toList());
        List<String> portfolios = portfolioRepository.findByResume_ResumeId(resume.getResumeId()).stream()
                .map(portfolio -> portfolio.getPortFile())
                .collect(Collectors.toList());
        PickResumeDTO pickResumeDTO = PickResumeDTO.builder()
                .personalId(personalUser.getPersonalId())
                .resumeId(resume.getResumeId())
                .resumeName(resume.getResumeName())
                .resumeDate(resume.getResumeDate())
                .resumePhoto(resume.getResumePhoto())
                .phoneNum(resume.getPhoneNum())
                .personalState(resume.getPersonalState())
                .techName(String.join(",", techNames))
                .jobName(String.join(",", jobNames))
                .educations(educations)
                .careers(careers)
                .curriculums(curriculums)
                .etcs(etcs)
                .archives(archives)
                .certificates(certificates)
                .portfolios(portfolios)
                .build();

        return pickResumeDTO;
    }

    // opencard를 받아서 해당 사용자가 Payment에 berry_point가 있는지 확인하고 berry_point가 부족하면 결제페이지로 넘어가게하고
    // berry_point가 있으면 포인트차감해서 Payment에 저장하고
    // 나머지 정보(seller, buyer, berry_bucket, pay_history_content, pay_history_date)는 PayHistory에 저장한다.
    // 만약 pay_history에 buyer_id가 산 seller_id가 존재한다면 PickCard를 눌렀을때
    // 바로 /pickzone/:personalId로 넘어갈 수 있게 구현해줘
    // card를 오픈했을 때 사용자 포인트 확인하고 차감하는 메서드
    @Override
    @Transactional
    public boolean handlePayment(OpenCardDTO openCardDTO) {
        // Buyer 정보 가져오기
        PersonalUser buyer = personalUserRepository.findByPersonalId(openCardDTO.getBuyerId());
        if(buyer == null) {
            throw new RuntimeException("personal user not found");
        }
        // Seller 정보 가져오기
        PersonalUser seller = personalUserRepository.findByPersonalId(openCardDTO.getSellerId());
        if(seller == null) {
            throw new RuntimeException("personal user not found");
        }
        // PayHistory에 buyerId와 sellerId가 존재하는지 확인
        if(payHistoryRepository.existsByBuyerIdAndSellerId(buyer.getPersonalId(), seller.getPersonalId())){
            return true;    // 이미 결제가 존재함
        }
        // 현재 포인트 확인
        Payment buyerPayment = paymentRepository.findByPersonalUser_PersonalId(buyer.getPersonalId());
        if(buyerPayment == null) {
            throw new RuntimeException("personal user not found");
        }
        // 50보다 적으면 error
        if(buyerPayment.getBerryPoint() < 50){
            throw new IllegalArgumentException("Not enough points");
        }

        // 포인트 차감
        buyerPayment.subtractBerryPoints(50L);
        paymentRepository.save(buyerPayment);

        // 결제 내역 저장
        PayHistory payHistory = PayHistory.builder()
                .sellerId(seller.getPersonalId())
                .buyerId(buyer.getPersonalId())
                .berryBucket("-50")
                .payHistoryContent(openCardDTO.getPayHistoryContent())
                .payHistoryDate(openCardDTO.getPayHistoryDate())
                .personalUser(buyer)
                .build();

        payHistoryRepository.save(payHistory);
        return false;
    }

}
