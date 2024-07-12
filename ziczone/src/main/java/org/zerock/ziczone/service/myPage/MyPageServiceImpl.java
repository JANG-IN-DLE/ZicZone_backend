package org.zerock.ziczone.service.myPage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import org.zerock.ziczone.dto.mypage.*;
import org.zerock.ziczone.exception.mypage.CompanyNotFoundException;
import org.zerock.ziczone.exception.mypage.InvalidPasswordException;
import org.zerock.ziczone.exception.mypage.PersonalNotFoundException;
import org.zerock.ziczone.exception.mypage.UserNotFoundException;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements  MyPageService{

    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;
    private final PersonalUserRepository personalUserRepository;
    private final PaymentRepository paymentRepository;
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
        CompanyUser companyUser = getCompanyUserById(userId);
        return convertToCompanyUserDTO(user, companyUser);
    }

    /**
     * 마이페이지 기업유저 정보 수정
     * @param userId
     * @param companyUserUpdateDTO
     * @return
     */
    @Override
    public String updateCompanyUser(Long userId, CompanyUserUpdateDTO companyUserUpdateDTO) {
        User user = getUserById(userId);
        CompanyUser companyUser = getCompanyUserById(userId);

        // 기존 비밀번호 검증
        if (companyUserUpdateDTO.getCurrentPassword() != null) {
            if (!passwordEncoder.matches(companyUserUpdateDTO.getCurrentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }
        }else{ // 현재 비밀번호 입력 하지 않았을 경우
            throw new InvalidPasswordException("Current password is null");
        }
        // 새로운 비밀번호 검증
        if (companyUserUpdateDTO.getCurrentPassword() != null && !companyUserUpdateDTO.getCurrentPassword().isEmpty()) {
            hashPassword(companyUserUpdateDTO.getChangePassword());
        }

        CompanyUser updatedCompanyUser = companyUser.toBuilder()
                .companyAddr(companyUserUpdateDTO.getCompanyAddr())
                .companyLogo(companyUserUpdateDTO.getCompanyLogo())
                .user(user)
                .build();

        user = user.toBuilder()
                .userName(companyUserUpdateDTO.getUserName() != null ? companyUserUpdateDTO.getUserName() : user.getUserName())
                .userIntro(companyUserUpdateDTO.getUserIntro() != null ? companyUserUpdateDTO.getUserIntro() : user.getUserIntro())
                .password(companyUserUpdateDTO.getChangePassword() != null ? hashPassword(companyUserUpdateDTO.getChangePassword()) : user.getPassword())
                .build();

        userRepository.save(user);
        companyUserRepository.save(updatedCompanyUser);
        return "User Information Updated Successfully";
    }

    /**
     * 마이페이지 개인유저 정보 조회
     * @param userId 유저 아이디
     * @return PersonalUserDTO 개인 유저 정보
     */
    @Override
    public PersonalUserDTO getPersonalUserDTO(Long userId) {
        User user = getUserById(userId);
        PersonalUser personalUser = getPersonalUserById(userId);
        return convertToPersonalUserDTO(user, personalUser);
    }

    /**
     * 마이페이지 개인유저 정보 수정
     * @param userId
     * @param personalUserUpdateDTO
     * @return 성공 메시지
     */
    @Override
    public String updatePersonalUser(Long userId, PersonalUserUpdateDTO personalUserUpdateDTO) {
        User user = getUserById(userId);
        PersonalUser personalUser = getPersonalUserById(userId);

        // 기존 비밀번호 검증 조건문
        if (personalUserUpdateDTO.getCurrentPassword() != null) {
            if (!passwordEncoder.matches(personalUserUpdateDTO.getCurrentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }
        }else{ // 현재 비밀번호 입력 하지 않았을 경우
            throw new InvalidPasswordException("Current password is null");
        }

        // 새로운 비밀번호 검증
        if (personalUserUpdateDTO.getChangePassword() != null && !personalUserUpdateDTO.getChangePassword().isEmpty()) {
            validatePassword(personalUserUpdateDTO.getChangePassword());
        }

        PersonalUser updatedPersonalUser = PersonalUser.builder()
                //기존 아이디 유지
                .personalId(personalUser.getPersonalId())
                .user(user) // 기존 유저또한 유지
                .personalCareer(personalUserUpdateDTO.getPersonalCareer() != null ?
                        personalUserUpdateDTO.getPersonalCareer() : personalUser.getPersonalCareer())
                .isCompanyVisible(personalUserUpdateDTO.isCompanyVisible())
                .isPersonalVisible(personalUserUpdateDTO.isPersonalVisible())
                .gender(user.getPersonalUser().getGender())
                .build();

        user = user.toBuilder()
                .userIntro(personalUserUpdateDTO.getIntro() != null ? personalUserUpdateDTO.getIntro() : user.getUserIntro())
                .password(personalUserUpdateDTO.getChangePassword() != null ? hashPassword(personalUserUpdateDTO.getChangePassword()) : user.getPassword())
                .build();

        userRepository.save(user);
        personalUserRepository.save(updatedPersonalUser);

        return "User Information Updated Successfully";
    }

    /**
     * 구매한 이력서 목록 조회
     * @param userId 유저 아이디
     * @return AggregatedDataDTO 구매한 이력서 리스트
     */
    @Override
    public AggregatedDataDTO getAggregatedData(Long userId) {
        getPersonalUserById(userId);
        List<Long> buyerIds = payHistoryRepository.findBuyerIdsBySellerId(userId);

        List<PersonalUserDTO> personalUsers = personalUserRepository.findByUserIds(buyerIds)
                .stream()
                .map(this::convertToPersonalUserDTO)
                .toList();

        return AggregatedDataDTO.builder()
                .personalUsers(personalUsers)
                .build();
    }

    /**
     * Pick 탭 조회 (기업)
     * 기업의 Pick 탭에는 개인회원의 정보를 담는 카드를 보여주기때문에 Pick 페이지와 비슷한 폼을 사용
     * @param userId 유저 아이디
     * @return List<PersonalUserDTO> 개인 유저 정보 리스트
     */
    @Override
    public List<PersonalUserDTO> getPicksByCompanyUsers(Long userId) {
        User user = getUserById(userId);
        CompanyUser companyUser = getCompanyUserById(user.getUserId());
        List<PickAndScrap> picks = pickAndScrapRepository.findByCompanyUserAndPickTrue(companyUser);
        return picks.stream()
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

    /**
     * Scrap 탭 조회 (기업)
     * @param userId
     * @return
     */
    @Override
    public List<PersonalUserDTO> getScrapByCompanyUsers(Long userId) {
        User user = getUserById(userId);
        CompanyUser companyUser = getCompanyUserById(user.getUserId());
        List<PickAndScrap> picks = pickAndScrapRepository.findByCompanyUserAndScrapTrue(companyUser);
        return picks.stream()
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

//    나의 게시물 리스트 조회는 BoardService 에 작성되어 있습니다.

    /**
     * Pick 탭 조회 (개인)
     * @param userId 개인 유저 아이디
     * @return List<CompanyUserDTO> 개인 유저 정보 리스트
     */
    @Override
    public List<CompanyUserDTO> getPicksByPersonalUsers(Long userId) {
        User user = getUserById(userId);
        PersonalUser personalUser = getPersonalUserById(user.getUserId());
        List<PickAndScrap> picks = pickAndScrapRepository.findByPersonalUserAndPickTrue(personalUser);
        return picks.stream()
                .map(pick -> convertToCompanyUserDTO(pick.getCompanyUser().getUser(), pick.getCompanyUser()))
                .collect(Collectors.toList());
    }

    /**
     * 나의 댓글 리스트 조회
     * @param userId 개인 유저 아이디
     * @return List<MyCommentListDTO>
     */
    @Override
    public List<MyCommentListDTO> MyCommList(Long userId) {
        User userCheck = getUserById(userId);
        getPersonalUserById(userCheck.getUserId());
        List<Comment> comments = commentRepository.findByUserUserId(userId);

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

    @Override
    public ResumeDTO getResume(Long userId) {
        User user = getUserById(userId);
        PersonalUser personalUser = getPersonalUserById(user.getUserId());

        return null;
    }

    @Override
    public ResumeDTO setResume(Long userId, ResumeDTO resumeDTO) {
        return null;
    }

    @Override
    public List<CompanyUserDTO> getCompanyUserList() {
        List<CompanyUser> companyUsers = companyUserRepository.findAll();

        return companyUsers.stream()
                .map(companyUser -> {
                    User user = companyUser.getUser();
                    UserDTO userDTO = UserDTO.builder()
                            .userId(user.getUserId())
                            .email(user.getEmail())
                            .userName(user.getUserName())
                            .userIntro(user.getUserIntro())
                            .userType(null)
                            .build();

                    return CompanyUserDTO.builder()
                            .userId(user.getUserId())
                            .user(userDTO)
                            .companyId(companyUser.getCompanyId())
                            .companyNum(companyUser.getCompanyNum())
                            .companyAddr(companyUser.getCompanyAddr())
                            .companyLogo(companyUser.getCompanyLogo())
                            .companyCeo(companyUser.getCompanyCeo())
                            .companyYear(companyUser.getCompanyYear())
                            .build();
                })
                .collect(Collectors.toList());
    }


    //    --------------------------------------------------------------------- 형변환 메서드

    /**
     * 비밀번호 규칙 검증 메서드
     * @param password
     */
    private void validatePassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!Pattern.matches(passwordPattern, password)) {
            throw new InvalidPasswordException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
    }

    private PersonalUser getPersonalUserById(Long personalUserId) {
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(personalUserId);
        if (personalUser == null) {
            throw new PersonalNotFoundException("Personal User Not Found");
        }
        return personalUser;
    }

    private CompanyUser getCompanyUserById(Long companyUserId) {
        CompanyUser companyUser =  companyUserRepository.findByUser_UserId(companyUserId);
        if (companyUser == null) {
            throw new CompanyNotFoundException("Company User Not Found");
        }
        return companyUser;
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
