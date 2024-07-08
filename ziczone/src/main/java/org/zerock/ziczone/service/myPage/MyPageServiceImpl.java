package org.zerock.ziczone.service.myPage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.PickAndScrap;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.domain.board.Comment;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.Gender;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.tech.TechStack;
import org.zerock.ziczone.dto.help.CommentDTO;
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
    private final PasswordEncoder passwordEncoder;

    private String hashPassword(String password){
        return passwordEncoder.encode(password);
    }

    /**
     * 마이페이지 기업유저 정보 조회
     * @param userId 유저 아이디
     * @return CompanyUserDTO 기업 유저 정보
     */
    @Override
    public CompanyUserDTO getCompanyUserDTO(Long userId) {
        User user = getUserById(userId);
        CompanyUser companyUser = companyUserRepository.findByUser_UserId(userId);
        if (companyUser == null) {
            throw new RuntimeException("company not found");
        }

        return convertToCompanyUserDTO(user, companyUser);
    }

    /**
     * 마이페이지 기업유저 정보 수정
     * @param userId
     * @param companyUserUpdateDTO
     * @return
     */
    @Override
    public CompanyUserDTO updateCompanyUser(Long userId, CompanyUserUpdateDTO companyUserUpdateDTO) {
        User user = getUserById(userId);
        CompanyUser companyUser = companyUserRepository.findByUser_UserId(userId);
        if (companyUser == null) {
            throw new RuntimeException("company not found");
        }

        // 비밀번호 업데이트 처리
        if (companyUserUpdateDTO.getCompanyUserPassword() != null && !companyUserUpdateDTO.getCompanyUserPassword().isEmpty()) {
            String hashedPassword = hashPassword(companyUserUpdateDTO.getCompanyUserPassword());
            user = user.toBuilder()
                    .password(hashedPassword)
                    .build();
            userRepository.save(user);
        }

        CompanyUser updatedCompanyUser = companyUser.toBuilder()
                .companyAddr(companyUserUpdateDTO.getCompanyAddr())
                .companyLogo(companyUserUpdateDTO.getCompanyLogo())
                .build();

        user = user.toBuilder()
                .userName(companyUserUpdateDTO.getUserName() != null ? companyUserUpdateDTO.getUserName() : user.getUserName())
                .userIntro(companyUserUpdateDTO.getUserIntro() != null ? companyUserUpdateDTO.getUserIntro() : user.getUserIntro())
                .build();

        userRepository.save(user);
        companyUserRepository.save(updatedCompanyUser);
        return convertToCompanyUserDTO(user, updatedCompanyUser);
    }

    /**
     * 마이페이지 개인유저 정보 조회
     * @param userId 유저 아이디
     * @return PersonalUserDTO 개인 유저 정보
     */
    @Override
    public PersonalUserDTO getPersonalUserDTO(Long userId) {
        User user = getUserById(userId);
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        if (personalUser == null) {
            throw new RuntimeException("personal not found");
        }
        return convertToPersonalUserDTO(user, personalUser);
    }

    /**
     * 마이페이지 개인유저 정보 수정
     * @param userId
     * @param personalUserUpdateDTO
     * @return
     */
    @Override
    public PersonalUserDTO updatePersonalUser(Long userId, PersonalUserUpdateDTO personalUserUpdateDTO) {
        User user = getUserById(userId);
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        if (personalUser == null) {
            throw new RuntimeException("personal not found");
        }

        PersonalUser updatedPersonalUser = PersonalUser.builder()
                //기존 아이디 유지
                .personalId(personalUser.getPersonalId())
                .user(user) // 기존 유저또한 유지
                .personalCareer(personalUserUpdateDTO.getPersonalCareer())
                .isCompanyVisible(personalUserUpdateDTO.isCompanyVisible())
                .isPersonalVisible(personalUserUpdateDTO.isPersonalVisible())
                .gender(user.getPersonalUser().getGender())
                .build();

        User updatedUser = User.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .userIntro(personalUserUpdateDTO.getIntro() != null ? personalUserUpdateDTO.getIntro() : user.getUserIntro())
                .password(user.getPassword())
                .userType(user.getUserType())
                .build();

        if(personalUserUpdateDTO.getPersonalUserPassword() != null && !personalUserUpdateDTO.getPersonalUserPassword().isEmpty()) {
            String hashedPassword = hashPassword(personalUserUpdateDTO.getPersonalUserPassword());
            updatedUser = updatedUser.toBuilder()
                    .password(hashedPassword)
                    .build();
            /**
             * 빌더 패턴을 사용하여 updatedUser 객체를 업데이트하는 방식은 새 객체를 생성하는 것입니다.
             * toBuilder 메서드는 Lombok이 제공하는 기능으로, 기존 객체의 값을 복사하여 새로운 빌더를 생성합니다.
             * 이를 통해 기존 값은 유지하고 필요한 필드만 변경할 수 있습니다.
             * toBuilder 메서드를 사용하려면 Lombok의 @Builder 어노테이션을 클래스 수준에 추가하면서 toBuilder = true 속성을 설정해야 합니다.
             * 이는 Lombok이 해당 클래스에 대해 toBuilder 메서드를 생성하도록 지시합니다.
             *
             * 먼저, User 엔티티에 @Builder(toBuilder = true)를 추가해야 합니다.
             */


        }
        userRepository.save(updatedUser);
        personalUserRepository.save(updatedPersonalUser);
        return convertToPersonalUserDTO(updatedUser, updatedPersonalUser);
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
     * @param userId 유저 아이디
     * @return AggregatedDataDTO 구매한 이력서 리스트
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
                .map(this::convertToPersonalUserDTO)
                .collect(Collectors.toList());

        return AggregatedDataDTO.builder()
                .personalUsers(personalUsers)
                .build();
    }

    /**
     * Pick 탭 기업정보 리스트 조회
     * 기업의 Pick 탭에는 개인회원의 정보를 담는 카드를 보여주기때문에 Pick 페이지와 비슷한 폼을 사용
     * @param personalUserId 개인 유저 아이디
     * @return List<PersonalUserDTO> 개인 유저 정보 리스트
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
                            .resumes(null)
                            .jobPositions(jobPositionDTOS)
                            .techStacks(techStackDTOS)
                            .build();
                })
                .toList();
    }

//    나의 게시물 리스트 조회는 BoardService에

    /**
     * Pick 탭 개인정보 조회
     * @param personalUserId 개인 유저 아이디
     * @return List<CompanyUserDTO> 개인 유저 정보 리스트
     */
    @Override
    public List<CompanyUserDTO> getPicksByPersonalUsers(Long personalUserId) {
        CompanyUser companyUser = getCompanyUserById(personalUserId);
        List<PickAndScrap> pickAndScrapList = pickAndScrapRepository.findByCompanyUserAndPickTrue(companyUser);
        return pickAndScrapList.stream()
                .map(pick -> convertToCompanyUserDTO(pick.getCompanyUser().getUser(), pick.getCompanyUser()))
                .collect(Collectors.toList());
    }

    /**
     * 나의 댓글 리스트 조회
     * @param personalUserId 개인 유저 아이디
     * @return List<MyCommentListDTO>
     */
    @Override
    public List<MyCommentListDTO> MyCommList(Long personalUserId) {

        List<Comment> comments = commentRepository.findByUserUserId(personalUserId);

        return comments.stream()
                .map(comment -> {
                    User user = comment.getUser();
                    PersonalUser personalUser = user.getPersonalUser();
                    Board board = comment.getBoard();

                    return MyCommentListDTO.builder()
                            .commId(comment.getCommId())
                            .commContent(comment.getCommContent())
                            .commSelection(comment.isCommSelection())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .personalCareer(personalUser.getPersonalCareer())
                            .corrId(board.getCorrId())
                            .corrPoint(board.getCorrPoint())
                            .build();
                })
                .collect(Collectors.toList());
    }


    //    ---------------------------------------------------------------------
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));
    }

    private PersonalUser getPersonalUserById(Long personalUserId) {
        return personalUserRepository.findById(personalUserId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 개인 사용자 ID: " + personalUserId));
    }

    private CompanyUser getCompanyUserById(Long companyUserId) {
        return companyUserRepository.findById(companyUserId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 기업 사용자 ID: " + companyUserId));
    }

    private CompanyUserDTO convertToCompanyUserDTO(User user, CompanyUser companyUser) {
        UserDTO userDTO = convertUserToDTO(user);
        return CompanyUserDTO.builder()
                .userId(null)
                .companyId(companyUser.getCompanyId())
                .companyNum(companyUser.getCompanyNum())
                .companyAddr(companyUser.getCompanyAddr())
                .companyLogo(companyUser.getCompanyLogo())
                .companyCeo(companyUser.getCompanyCeo())
                .companyYear(companyUser.getCompanyYear())
                .user(userDTO)
                .build();
    }

    private PersonalUserDTO convertToPersonalUserDTO(User user, PersonalUser personalUser) {
        UserDTO userDTO = convertUserToDTO(user);

        List<JobPositionDTO> jobPositionDTOS = jobPositionRepository.findByPersonalUserPersonalId(personalUser.getPersonalId())
                .stream()
                .map(this::convertJobPositionToDTO)
                .collect(Collectors.toList());

        List<TechStackDTO> techStackDTOS = techStackRepository.findByPersonalUserPersonalId(personalUser.getPersonalId())
                .stream()
                .map(this::convertTechStackToDTO)
                .collect(Collectors.toList());

        return PersonalUserDTO.builder()
                .personalId(personalUser.getPersonalId())
                .personalCareer(personalUser.getPersonalCareer())
                .isPersonalVisible(personalUser.isPersonalVisible())
                .isCompanyVisible(personalUser.isCompanyVisible())
                .user(userDTO)
                .gender(personalUser.getGender().name())
                .resumes(null)
                .jobPositions(jobPositionDTOS)
                .techStacks(techStackDTOS)
                .build();
    }

    private PersonalUserDTO convertToPersonalUserDTO(PersonalUser personalUser) {
        return convertToPersonalUserDTO(personalUser.getUser(), personalUser);
    }

    private UserDTO convertUserToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .userType(user.getUserType().name())
                .userIntro(user.getUserIntro())
                .build();
    }

    private JobPositionDTO convertJobPositionToDTO(JobPosition jobPosition) {
        JobDTO jobDTO = JobDTO.builder()
                .jobId(jobPosition.getJob().getJobId())
                .jobName(jobPosition.getJob().getJobName())
                .build();

        return JobPositionDTO.builder()
                .userJobId(jobPosition.getUserJobId())
                .job(jobDTO)
                .build();
    }

    private TechStackDTO convertTechStackToDTO(TechStack techStack) {
        TechDTO techDTO = TechDTO.builder()
                .techId(techStack.getTech().getTechId())
                .techName(techStack.getTech().getTechName())
                .techUrl(techStack.getTech().getTechUrl())
                .build();

        return TechStackDTO.builder()
                .userTechId(techStack.getUserTechId())
                .tech(techDTO)
                .build();
    }

    private PersonalUser convertPersonalUserToEntity(PersonalUserDTO personalUserDTO) {
        return PersonalUser.builder()
                .personalCareer(personalUserDTO.getPersonalCareer())
                .isPersonalVisible(personalUserDTO.isPersonalVisible())
                .isCompanyVisible(personalUserDTO.isCompanyVisible())
                .gender(Gender.valueOf(personalUserDTO.getGender()))
                .user(userRepository.findById(personalUserDTO.getUser().getUserId()).orElse(null))
                .build();
    }
}
