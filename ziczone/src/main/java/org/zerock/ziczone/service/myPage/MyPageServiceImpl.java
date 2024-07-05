package org.zerock.ziczone.service.myPage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.PickAndScrap;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.Gender;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.tech.TechStack;
import org.zerock.ziczone.dto.mypage.*;
import org.zerock.ziczone.repository.AppPaymentRepository;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.PickAndScrapRepository;
import org.zerock.ziczone.repository.application.ResumeRepository;
import org.zerock.ziczone.repository.board.CommentRepository;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.job.JobRepository;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;
import org.zerock.ziczone.repository.tech.TechRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements  MyPageService{

    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;
    private final PersonalUserRepository personalUserRepository;
    private final PaymentRepository paymentRepository;
    private final AppPaymentRepository appPaymentRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final TechRepository techRepository;;
    private final PickAndScrapRepository pickAndScrapRepository;
    private final CommentRepository commentRepository;
    private final JobPositionRepository jobPositionRepository;
    private final TechStackRepository techStackRepository;
    private final PayHistoryRepository payHistoryRepository;

    //

    /**
     * PersonalUser 엔티티를 PersonalUserDTO로 변환하는 메서드
     *
     * @param personalUser 변환할 PersonalUser 엔티티
     * @return 변환된 PersonalUserDTO
     */
    private PersonalUserDTO convertPersonalUserToDTO(PersonalUser personalUser) {
        List<JobPositionDTO> jobPositionDTOS = personalUser.getJobPositions().stream()
                .map(this::convertJobPositionToDTO)
                .collect(Collectors.toList());

        UserDTO userDTO = convertUserToDTO(personalUser.getUser());

        return PersonalUserDTO.builder()
                .personalId(personalUser.getPersonalId())
                .personalCareer(personalUser.getPersonalCareer())
                .isPersonalVisible(personalUser.isPersonalVisible())
                .isCompanyVisible(personalUser.isCompanyVisible())
                .gender(personalUser.getGender().name())
                .user(userDTO)
                .jobPositions(jobPositionDTOS)
                .build();
    }

    /**
     * JobPosition 엔티티를 JobPositionDTO로 변환하는 메서드
     *
     * @param jobPosition 변환할 JobPosition 엔티티
     * @return 변환된 JobPositionDTO
     */
    private JobPositionDTO convertJobPositionToDTO(JobPosition jobPosition) {
        return JobPositionDTO.builder()
                .userJobId(jobPosition.getUserJobId())
                .job(JobDTO.builder()
                        .jobId(jobPosition.getJob().getJobId())
                        .jobName(jobPosition.getJob().getJobName())
                        .build())
                .build();
    }
    private TechStackDTO convertTechStackToDTO(TechStack techStack) {
        return TechStackDTO.builder()
                .userTechId(techStack.getUserTechId())
                .tech(TechDTO.builder()
                        .techId(techStack.getTech().getTechId())
                        .techName(techStack.getTech().getTechName())
                        .techUrl(techStack.getTech().getTechUrl())
                        .build())
                .build();
    }



    /**
     * User 엔티티를 UserDTO로 변환하는 메서드
     *
     * @param user 변환할 User 엔티티
     * @return 변환된 UserDTO
     */
    private UserDTO convertUserToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .userType(user.getUserType().name())
                .userIntro(user.getUserIntro())
                .build();
    }

    /**
     * PersonalUserDTO를 PersonalUser 엔티티로 변환하는 메서드
     *
     * @param personalUserDTO 변환할 PersonalUserDTO
     * @return 변환된 PersonalUser 엔티티
     */
    private PersonalUser convertPersonalUserToEntity(PersonalUserDTO personalUserDTO) {
        return PersonalUser.builder()
                .personalCareer(personalUserDTO.getPersonalCareer())
                .isPersonalVisible(personalUserDTO.getIsPersonalVisible())
                .isCompanyVisible(personalUserDTO.getIsCompanyVisible())
                .gender(Gender.valueOf(personalUserDTO.getGender()))
                .user(userRepository.findById(personalUserDTO.getUser().getUserId()).orElse(null))
                .build();
    }

    //


    /**
     * 마이페이지 기업유저 정보 조회
     * @param userId 유저 아이디
     * @return CompanyUserDTO 기업 유저 정보
     */
    @Override
    public CompanyUserDTO getCompanyUserDTO(Long userId) {
        User user = userRepository.findByUserId(userId);
        if(user == null) {
            throw new RuntimeException("user not found") ;
        }
        CompanyUser companyUser = companyUserRepository.findByUser_UserId(userId);
        if(companyUser == null) {
            throw new RuntimeException("company not found") ;
        }
        return CompanyUserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .userIntro(user.getUserIntro())
                .companyId(companyUser.getCompanyId())
                .companyNum(companyUser.getCompanyNum())
                .companyAddr(companyUser.getCompanyAddr())
                .companyLogo(companyUser.getCompanyLogo())
                .companyCeo(companyUser.getCompanyCeo())
                .companyYear(companyUser.getCompanyYear())
                .build();
    }

    /**
     * 마이페이지 개인유저 정보 조회
     * @param userId 유저 아이디
     * @return PersonalUserDTO 개인 유저 정보
     */
    @Override
    public PersonalUserDTO getPersonalUserDTO(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        if (personalUser == null) {
            throw new RuntimeException("personal not found");
        }
        UserDTO userDTO = convertUserToDTO(user);

        List<JobPositionDTO> jobPositionDTOS = jobPositionRepository.findByPersonalUserPersonalId(personalUser.getPersonalId())
                .stream()
                .map(this::convertJobPositionToDTO)
                .toList();

        List<TechStackDTO> techStackDTOS = techStackRepository.findByPersonalUserPersonalId(personalUser.getPersonalId())
                .stream()
                .map(this::convertTechStackToDTO)
                .toList();

        return PersonalUserDTO.builder()
                .personalId(personalUser.getPersonalId())
                .personalCareer(personalUser.getPersonalCareer())
                .isPersonalVisible(personalUser.isPersonalVisible())
                .isCompanyVisible(personalUser.isCompanyVisible())
                .gender(personalUser.getGender().name())
                .user(userDTO)
                .jobPositions(jobPositionDTOS)
                .techStacks(techStackDTOS)
                .build();
    }


    /**
     * 남은 포인트 조회 07-03_포인트 테이블 재 수정 예정
     * @param userId 유저 아이디
     * @return PersonalUserPointDTO 남은 포인트 정보
     */
    @Override
    public Optional<PersonalUserPointDTO> getPersonalUserRemainingPoints(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = userOptional.get();
        Long totalBerryPoints = paymentRepository.findTotalBerryPointsByUserId(userId).orElse(0L);
        Long totalBerryBucket = appPaymentRepository.findTotalBerryBuketByUserId(userId).orElse(0L);

        Long remainingPoints = totalBerryBucket - totalBerryPoints;

        PersonalUserPointDTO dto = PersonalUserPointDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .remainingPoints(remainingPoints)
                .build();
        return Optional.of(dto);
    }



    /**
     * 지원서 개인 공개 설정 유저 아이디 리스트 조회
     * @return List<Long> 개인 유저 아이디 리스트
     */
    @Override
    public List<Long> getVisiblePersonalIds() {
        return personalUserRepository.findPersonalUserIdsByIsPersonalVisibleTrue();
    }

    /**
     * 지원서 기업 공개 설정 유저 아이디 리스트 조회
     * @return List<Long> 기업 유저 아이디 리스트
     */
    @Override
    public List<Long> getVisibleCompanyIds() {
        return personalUserRepository.findPersonalUserIdsByIsCompanyVisibleTrue();
    }


    /**
     * 구매한 이력서 목록 조회
     * @param userId  유저 아이디
     * @return List<ResumeDTO> 구매한 이력서 리스트
     */
    @Override
    public AggregatedDataDTO getAggregatedData(Long userId) {
        List<Long> sellerIds = payHistoryRepository.findByBuyerId(userId)
                .stream()
                .map(PayHistory::getSellerId)
                .distinct()
                .collect(Collectors.toList());

        List<PersonalUserDTO> personalUsers = personalUserRepository.findByPersonalIdIn(sellerIds)
                .stream()
                .map(pu -> {
                    List<JobPositionDTO> jobPositionDTOS = jobPositionRepository.findByPersonalUserPersonalId(pu.getPersonalId())
                            .stream()
                            .map(this::convertJobPositionToDTO)
                            .collect(Collectors.toList());

                    List<TechStackDTO> techStackDTOS = techStackRepository.findByPersonalUserPersonalId(pu.getPersonalId())
                            .stream()
                            .map(this::convertTechStackToDTO)
                            .collect(Collectors.toList());

                    return PersonalUserDTO.builder()
                            .personalId(pu.getPersonalId())
                            .personalCareer(pu.getPersonalCareer())
                            .gender(pu.getGender().name())
                            .isPersonalVisible(pu.isPersonalVisible())
                            .isCompanyVisible(pu.isCompanyVisible())
                            .user(convertUserToDTO(pu.getUser()))
                            .jobPositions(jobPositionDTOS)
                            .techStacks(techStackDTOS)
                            .build();
                })
                .collect(Collectors.toList());

        List<ResumeDTO> resumes = resumeRepository.findByPersonalUserPersonalIdIn(sellerIds)
                .stream()
                .map(r -> ResumeDTO.builder()
                        .resumeId(r.getResumeId())
                        .personalState(r.getPersonalState())
                        .phoneNum(r.getPhoneNum())
                        .resumeCreate(r.getResumeCreate())
                        .resumeDate(r.getResumeDate())
                        .resumeName(r.getResumeName())
                        .resumePhoto(r.getResumePhoto())
                        .resumeUpdate(r.getResumeUpdate())
                        .personalId(r.getPersonalUser().getPersonalId())
                        .build())
                .collect(Collectors.toList());

        List<JobPositionDTO> jobs = jobPositionRepository.findByPersonalUserPersonalIdIn(sellerIds)
                .stream()
                .map(this::convertJobPositionToDTO)
                .collect(Collectors.toList());

        List<TechStackDTO> techs = techStackRepository.findByPersonalUserPersonalIdIn(sellerIds)
                .stream()
                .map(this::convertTechStackToDTO)
                .collect(Collectors.toList());

        return AggregatedDataDTO.builder()
                .personalUsers(personalUsers)
                .resumes(resumes)
                .jobs(jobs)
                .techs(techs)
                .build();
    }




    /**
     * Pick 탭 기업정보 리스트
     * @param personalUserId 유저 아이디
     * @return List<CompanyUserDTO> 회사 유저 정보 리스트
     */
    @Override
    public List<PersonalUserDTO> getPicksByCompanyUsers(Long personalUserId) {
        PersonalUser personalUser = personalUserRepository.findById(personalUserId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 개인 사용자 ID: " + personalUserId));

        List<PickAndScrap> pickAndScrapList = pickAndScrapRepository.findByPersonalUserAndPickTrue(personalUser);

        return pickAndScrapList.stream()
                .map(pickAndScrap -> {
                    PersonalUser pUser = pickAndScrap.getPersonalUser();
                    UserDTO userDTOs = UserDTO.builder()
                            .userId(pUser.getUser().getUserId())
                            .email(pUser.getUser().getEmail())
                            .userName(pUser.getUser().getUserName())
                            .userIntro(pUser.getUser().getUserIntro())
                            .userType(pUser.getUser().getUserType().name())
                            .build();

                    List<JobPositionDTO> jobPositionDTOS = jobPositionRepository.findByPersonalUserPersonalId(pUser.getPersonalId())
                            .stream()
                            .map(this::convertJobPositionToDTO)
                            .toList();

                    List<TechStackDTO> techStackDTOS = techStackRepository.findByPersonalUserPersonalId(pUser.getPersonalId())
                            .stream()
                            .map(this::convertTechStackToDTO)
                            .toList();

                    return PersonalUserDTO.builder()
                            .personalId(pUser.getPersonalId())
                            .personalCareer(pUser.getPersonalCareer())
                            .isPersonalVisible(pUser.isPersonalVisible())
                            .isCompanyVisible(pUser.isCompanyVisible())
                            .gender(pUser.getGender().name())
                            .user(userDTOs)
                            .jobPositions(jobPositionDTOS)
                            .techStacks(techStackDTOS)
                            .build();
                })
                .toList();
    }


    /**
     * Pick 탭 개인정보 조회
     * @param personalUserId 개인 유저 아이디
     * @return List<PersonalUserDTO> 개인 유저 정보 리스트
     */
    @Override
    public List<CompanyUserDTO> getPicksByPersonalUsers(Long personalUserId) {
        CompanyUser companyUser = companyUserRepository.findById(personalUserId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 기업 사용자 ID: " + personalUserId));
        List<PickAndScrap> pickAndScrapList = pickAndScrapRepository.findByCompanyUserAndPickTrue(companyUser);

        return pickAndScrapList.stream()
                .map(pick -> {
                    CompanyUser cUser = pick.getCompanyUser();
                    UserDTO userDTOs = UserDTO.builder()
                            .userId(cUser.getUser().getUserId())
                            .email(cUser.getUser().getEmail())
                            .userName(cUser.getUser().getUserName())
                            .userIntro(cUser.getUser().getUserIntro())
                            .userType(cUser.getUser().getUserType().name())
                            .build();

                    return CompanyUserDTO.builder()
                            .userId(companyUser.getUser().getUserId())
                            .companyId(cUser.getCompanyId())
                            .companyNum(cUser.getCompanyNum())
                            .companyYear(cUser.getCompanyYear())
                            .companyLogo(cUser.getCompanyLogo())
                            .companyCeo(cUser.getCompanyCeo())
                            .companyAddr(cUser.getCompanyAddr())
                            .userName(cUser.getUser().getUserName())
                            .email(cUser.getUser().getEmail())
                            .userIntro(cUser.getUser().getUserIntro())
                            .userId(cUser.getUser().getUserId())
                            .build();
                })
                .toList();
    }


}
