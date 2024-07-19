package org.zerock.ziczone.service.pick;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.PickAndScrap;
import org.zerock.ziczone.domain.application.Career;
import org.zerock.ziczone.domain.application.Education;
import org.zerock.ziczone.domain.application.Resume;
import org.zerock.ziczone.domain.job.Job;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.pick.*;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.PickAndScrapRepository;
import org.zerock.ziczone.repository.application.*;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.job.JobRepository;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;

import javax.transaction.Transactional;
import java.util.*;
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
    private final PickAndScrapRepository pickAndScrapRepository;
    private final CompanyUserRepository companyUserRepository;

    // (로그인 안되었을때) 메인페이지에서 pickCards 전송
    @Override
    public List<PickCardDTO> getPickCards() {
        // 최신 resume 정보를 포함하는 PersonalUser 리스트를 가져오기
        List<Resume> latestResumes = resumeRepository.findTop4ByOrderByResumeUpdateDesc();

        return latestResumes.stream().map(resume -> {
            PersonalUser user = resume.getPersonalUser();

            List<String> techNames = techStackRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(techStack -> techStack.getTech().getTechName())
                    .collect(Collectors.toList());

            List<String> techUrls = techStackRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(techStack -> techStack.getTech().getTechUrl())
                    .collect(Collectors.toList());

            List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(jobPosition -> jobPosition.getJob().getJobName())
                    .collect(Collectors.toList());

            List<PickAndScrap> pickAndScraps = pickAndScrapRepository.findByPersonalUser(user);
            List<Boolean> scrapList = pickAndScraps.stream().map(PickAndScrap::isScrap).collect(Collectors.toList());
            List<Boolean> pickList = pickAndScraps.stream().map(PickAndScrap::isPick).collect(Collectors.toList());
            List<Long> companyIdList = pickAndScraps.stream()
                    .map(pickAndScrap -> pickAndScrap.getCompanyUser().getCompanyId())
                    .collect(Collectors.toList());

            return PickCardDTO.builder()
                    .userId(user.getUser().getUserId())
                    .personalId(user.getPersonalId())
                    .userName(user.getUser().getUserName())
                    .userIntro(user.getUser().getUserIntro())
                    .gender(user.getGender())
                    .personalCareer(user.getPersonalCareer())
                    .techName(String.join(",", techNames))
                    .techUrl(String.join(",", techUrls))
                    .jobName(String.join(",", jobNames))
                    .scrap(scrapList)
                    .pick(pickList)
//                    .companyId(companyIdList)
                    .resumeUpdate(resume.getResumeUpdate())
                    .build();
        }).collect(Collectors.toList());
    }

    // (personal회원이 로그인했을 때) pickzone 회원 card data 가져오는 메서드
    @Override
    public List<PickCardDTO> getPersonalPickCards(Long loggedInUserId) {
// 최신 resume 정보를 포함하는 PersonalUser 리스트를 가져오기
        List<Resume> latestResumes = resumeRepository.findAllByPersonalUserIsPersonalVisibleTrueOrderByResumeUpdateDesc();
        // 개인회원의 정보를 가져온다.
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(loggedInUserId);
        // 개인회원의 berryPoint를 가져온다.
        Payment payment = paymentRepository.findByPersonalUser_PersonalId(personalUser.getPersonalId());
        Integer userBerryPoint = (payment != null && payment.getBerryPoint() != null) ? payment.getBerryPoint() : 0;

        return latestResumes.stream().map(resume -> {
            PersonalUser user = resume.getPersonalUser();

            List<String> techNames = techStackRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(techStack -> techStack.getTech().getTechName())
                    .collect(Collectors.toList());

            List<String> techUrls = techStackRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(techStack -> techStack.getTech().getTechUrl())
                    .collect(Collectors.toList());

            List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(jobPosition -> jobPosition.getJob().getJobName())
                    .collect(Collectors.toList());

            List<PickAndScrap> pickAndScraps = pickAndScrapRepository.findByPersonalUser(user);
            List<Boolean> scrapList = pickAndScraps.stream().map(PickAndScrap::isScrap).collect(Collectors.toList());
            List<Boolean> pickList = pickAndScraps.stream().map(PickAndScrap::isPick).collect(Collectors.toList());
            List<Long> companyIdList = pickAndScraps.stream()
                    .map(pickAndScrap -> pickAndScrap.getCompanyUser().getCompanyId())
                    .collect(Collectors.toList());
            // 결제 내역 추출
            List<Long> payHistoryId = payHistoryRepository.findBySellerIdAndBuyerId(user.getPersonalId(), personalUser.getPersonalId()).stream()
                    .map(PayHistory::getPayHistoryId).collect(Collectors.toList());


            return PickCardDTO.builder()
                    .userId(user.getUser().getUserId())
                    .personalId(user.getPersonalId())
                    .userName(user.getUser().getUserName())
                    .userIntro(user.getUser().getUserIntro())
                    .gender(user.getGender())
                    .personalCareer(user.getPersonalCareer())
                    .techName(String.join(",", techNames))
                    .techUrl(String.join(",", techUrls))
                    .jobName(String.join(",", jobNames))
                    .scrap(scrapList)
                    .pick(pickList)
//                    .companyId(companyIdList)
                    .resumeUpdate(resume.getResumeUpdate())
                    .payHistoryId(payHistoryId)
                    .berryPoint(userBerryPoint)
                    .build();
        }).collect(Collectors.toList());

    }

    // (company회원이 로그인했을 때) pickzone 회원 card data 가져오는 메서드
    @Override
    public List<PickCardDTO> getCompanyPickCards(Long loggedInUserId) {
        // loggedInUserId를 사용하여 CompanyUser를 찾음
        CompanyUser loggedInCompanyUser = companyUserRepository.findByUser_UserId(loggedInUserId);
        if(loggedInCompanyUser == null) {
            throw new RuntimeException("company user not found");
        }
        // isCompanyvisible이 true인 최신 resume 정보를 포함하는 PersonalUser리스트를 가져옴
        List<Resume> latestResumes = resumeRepository.findAllByPersonalUserIsCompanyVisibleTrueOrderByResumeUpdateDesc();

        return latestResumes.stream().map(resume -> {
            PersonalUser user = resume.getPersonalUser();

            List<String> techNames = techStackRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(techStack -> techStack.getTech().getTechName())
                    .collect(Collectors.toList());

            List<String> techUrls = techStackRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(techStack -> techStack.getTech().getTechUrl())
                    .collect(Collectors.toList());

            List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(user.getPersonalId()).stream()
                    .map(jobPosition -> jobPosition.getJob().getJobName())
                    .collect(Collectors.toList());
            // loggedInCompanyUser와 PersonalUser에 해당하는 PickAndScrap 엔티티를 찾음
            Optional<PickAndScrap> pickAndScrap = pickAndScrapRepository.findByCompanyUserAndPersonalUser(loggedInCompanyUser, user);
            Boolean scrap = false;
            Boolean pick = false;
            Long companyId = null;
            if(pickAndScrap.isPresent()) {
                scrap = pickAndScrap.get().isScrap();
                pick = pickAndScrap.get().isPick();
                companyId = pickAndScrap.get().getCompanyUser().getCompanyId();
            }

//            List<PickAndScrap> pickAndScraps = pickAndScrapRepository.findByPersonalUser(user);
//            List<Boolean> scrapList = pickAndScraps.stream().map(PickAndScrap::isScrap).collect(Collectors.toList());
//            List<Boolean> pickList = pickAndScraps.stream().map(PickAndScrap::isPick).collect(Collectors.toList());
//            List<Long> companyIdList = pickAndScraps.stream()
//                    .map(pickAndScrap -> pickAndScrap.getCompanyUser().getCompanyId())
//                    .collect(Collectors.toList());

            return PickCardDTO.builder()
                    .userId(user.getUser().getUserId())
                    .personalId(user.getPersonalId())
                    .companyId(companyId)
                    .userName(user.getUser().getUserName())
                    .userIntro(user.getUser().getUserIntro())
                    .gender(user.getGender())
                    .personalCareer(user.getPersonalCareer())
                    .techName(String.join(",", techNames))
                    .techUrl(String.join(",", techUrls))
                    .jobName(String.join(",", jobNames))
                    .scrap(Collections.singletonList(scrap))
                    .pick(Collections.singletonList(pick))
//                    .scrap(scrapList)
//                    .pick(pickList)
//                    .companyId(companyIdList)
                    .resumeUpdate(resume.getResumeUpdate())
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
//  (CompanyId로 로그인했을 경우) pickDetailzone 왼쪽 회원정보 가져오는 메서드
    @Override
    public PickDetailDTO getPickCardsById(Long loggedInUserId, Long personalId) {
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);
        if(personalUser == null) {
            throw new RuntimeException("personal user not found");
        }
        CompanyUser companyUser = companyUserRepository.findByUser_UserId(loggedInUserId);
        if(companyUser == null) {
            throw new RuntimeException("personal user not found");
        }

        List<String> techNames = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechName())
                .collect(Collectors.toList());
        // techUrl 받아오는 거 추가
        List<String> techUrls = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechUrl())
                .collect(Collectors.toList());
        List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(jobPosition -> jobPosition.getJob().getJobName())
                .collect(Collectors.toList());
        // personalUser가 pickAndScrap에서 유니크할 필요가 없어. personalUser와 pickAndScrap은 1대다 관계야. 문제를 해결해줘.
        Optional<PickAndScrap> pickAndScrapOptional = pickAndScrapRepository.findByCompanyUserAndPersonalUser(companyUser, personalUser);
        // 가져온 pickAndScrapList에서 scrap과 pick을 뽑아서 build

        Boolean scrap = false;
        Boolean pick = false;

        if (pickAndScrapOptional.isPresent()) {
            PickAndScrap pickAndScrap = pickAndScrapOptional.get();
            scrap = pickAndScrap.isScrap();
            pick = pickAndScrap.isPick();
        }

        PickDetailDTO pickDetailDTO = PickDetailDTO.builder()
                .userId(personalUser.getUser().getUserId())
                .personalId(personalUser.getPersonalId())
                .userName(personalUser.getUser().getUserName())
                .userIntro(personalUser.getUser().getUserIntro())
                .gender(personalUser.getGender())
                .personalCareer(personalUser.getPersonalCareer())
                .techName(String.join(",", techNames))
                .techUrl(String.join(",", techUrls))
                .jobName(String.join(",", jobNames))
                .scrap(scrap)
                .pick(pick)
                .build();

        return pickDetailDTO;
    }

    // (PersonalId로 로그인했을 경우) pickDetailzone 왼쪽 회원정보 가져오는 메서드
    @Override
    public PickPersonalDetailDTO getPickCardsByPersonalId(Long loggedInUserId, Long personalId) {
        PersonalUser loggedInPersonalUser = personalUserRepository.findByUser_UserId(loggedInUserId);
        if(loggedInPersonalUser == null) {
            throw new RuntimeException("personal user not found");
        }
        PersonalUser personalUser = personalUserRepository.findByPersonalId(personalId);
        if(personalUser == null) {
            throw new RuntimeException("personal user not found");
        }
        List<String> techNames = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechName())
                .collect(Collectors.toList());
        // techUrl 받아오는 거 추가
        List<String> techUrls = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechUrl())
                .collect(Collectors.toList());
        List<String> jobNames = jobPositionRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(jobPosition -> jobPosition.getJob().getJobName())
                .collect(Collectors.toList());
        // payHistoryId에 결제한 내역이 있으면 바로 Detail로 들어갈 수 있게 하기 위해
        // 아 여기서 보내면 안되고 pickzone에서 card호출할때 거기서 보내야지
        // 지워도 될듯
        Optional<PayHistory> payHistoryOptional = payHistoryRepository.findByBuyerIdAndSellerId(loggedInPersonalUser.getPersonalId(), personalId);

        PickPersonalDetailDTO pickPersonalDetailDTO = PickPersonalDetailDTO.builder()
                .userId(personalUser.getUser().getUserId())
                .personalId(personalUser.getPersonalId())
                .userName(personalUser.getUser().getUserName())
                .userIntro(personalUser.getUser().getUserIntro())
                .gender(personalUser.getGender())
                .personalCareer(personalUser.getPersonalCareer())
                .techName(String.join(",", techNames))
                .techUrl(String.join(",", techUrls))
                .jobName(String.join(",", jobNames))
                .build();

        return pickPersonalDetailDTO;
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
        // techUrl 받아오는 거 추가
        List<String> techUrls = techStackRepository.findByPersonalUserPersonalId(personalId).stream()
                .map(techStack -> techStack.getTech().getTechUrl())
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
                .map(portfolio -> portfolio.getPortFileUrl())
                .collect(Collectors.toList());
        PickResumeDTO pickResumeDTO = PickResumeDTO.builder()
                .personalId(personalUser.getPersonalId())
                .resumeId(resume.getResumeId())
                .resumeName(resume.getResumeName())
                .resumeDate(resume.getResumeDate())
                .resumePhoto(resume.getResumePhotoUrl())
                .resumeEmail(resume.getResumeEmail())
                .phoneNum(resume.getPhoneNum())
                .personalState(resume.getPersonalStateUrl())
                .techName(String.join(",", techNames))
                .techUrl(String.join(",", techUrls))
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
        PersonalUser buyer = personalUserRepository.findByUser_UserId(openCardDTO.getBuyerId());
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
        buyerPayment.subtractBerryPoints(50);
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

    // scrap데이터 저장하는 메서드
    @Transactional
    @Override
    public PickAndScrapDTO scrapUser(PickAndScrapDTO pickAndScrapDTO) {

        CompanyUser companyUser = companyUserRepository.findByUser_UserId(pickAndScrapDTO.getUserId());
        if(companyUser == null) {
            throw new RuntimeException("companyUser user not found");
        }
        PersonalUser personalUser = personalUserRepository.findByPersonalId(pickAndScrapDTO.getPersonalId());
        if(personalUser == null) {
            throw new RuntimeException("personalUser user not found");
        }

        Optional<PickAndScrap> existingPickAndScrap = pickAndScrapRepository.findByCompanyUserAndPersonalUser(companyUser, personalUser);

        PickAndScrap pickAndScrap;
        if(existingPickAndScrap.isPresent()) {
            // 존재하면 scrap을 확인해서 true이면 false로 false면 true로 구현
            pickAndScrap = existingPickAndScrap.get();
            pickAndScrap.toggleScrap();
            log.info(pickAndScrap);
        }else{

            pickAndScrap = PickAndScrap.builder()
                    .companyUser(companyUser)
                    .personalUser(personalUser)
                    .scrap(true)
                    .build();
        }

        pickAndScrapRepository.save(pickAndScrap);

        return PickAndScrapDTO.builder()
                .userId(companyUser.getUser().getUserId())
                .personalId(personalUser.getPersonalId())
                .scrap(pickAndScrap.isScrap())
                .build();
    }

    // pick 데이터 저장
    @Transactional
    @Override
    public PickAndScrapDTO pickUser(PickAndScrapDTO pickAndScrapDTO) {
        CompanyUser companyUser = companyUserRepository.findByUser_UserId(pickAndScrapDTO.getUserId());
        PersonalUser personalUser = personalUserRepository.findByPersonalId(pickAndScrapDTO.getPersonalId());
        if(companyUser == null || personalUser == null){
            throw new RuntimeException("User not found");
        }
        Optional<PickAndScrap> pickAndScrapOptional = pickAndScrapRepository.findByCompanyUserAndPersonalUser(companyUser, personalUser);
        PickAndScrap pickAndScrap;
        if(pickAndScrapOptional.isPresent()) {
            pickAndScrap = pickAndScrapOptional.get();
            pickAndScrap.togglePick();
        }else{
            pickAndScrap = PickAndScrap.builder()
                    .companyUser(companyUser)
                    .personalUser(personalUser)
                    .pick(true)
                    .build();
        }
        pickAndScrapRepository.save(pickAndScrap);

        return PickAndScrapDTO.builder()
                .userId(companyUser.getUser().getUserId())
                .personalId(personalUser.getPersonalId())
                .pick(pickAndScrap.isPick())
                .build();

    }



}
