package org.zerock.ziczone.service.myPage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import org.zerock.ziczone.service.storage.StorageService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final StorageService storageService;

    private String hashPassword(String password){
        return passwordEncoder.encode(password);
    }

    /**
     * 비밀번호 확인 해서 회원 유혈별로 저장된 내역 리턴으로 제공
     * @param userId
     * @param json
     * @return
     */
    @Override
    public Map<String, Object> PasswordCheck(Long userId, Map<String, Object> json) {
        String role = (String) json.get("role");
        String password = (String) json.get("password");

        // 유저가 가입되어있는지 확인하는 로직 없을 경우 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", userId)));

        // 유저 테이블의 암호화된 비밀번호와 유저가 입력한 비밀번호가 매치(값은 값)인지 비교 틀리면 예외 발생
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidPasswordException("Current password is incorrect");
        }

        Map<String, Object> resultJson = new HashMap<>();

        // 기업회원의 경우
        /**  "COMPANY".equals(role) 방식과
         *   role.equals("COMPANY") 방식의 차이점은 NULL예외 발생의 차이가 있습니다.
         *   객체를 기준으로 비교하면 null을 반환하여 예외가 발생할 수 있다.
         *   문자열을 기준으로 비교하면 null을 발생시키지 않습니다.
         *
         */
        if("COMPANY".equals(role)){
            CompanyUser companyUser = companyUserRepository.findByUser_UserId(userId);
            if (companyUser == null){
                throw new CompanyNotFoundException("Company User Not Found");
            }
            CompanyUserDTO companyUserDTO = convertToCompanyUserDTO(user,companyUser);
            resultJson.put("name", companyUserDTO.getUser().getUserName());
            resultJson.put("companyLogoUrl", companyUserDTO.getCompanyLogoUrl());
            resultJson.put("companyAddr", companyUserDTO.getCompanyAddr());
//            개인회원의 경우
        }else if("PERSONAL".equals(role)){
            PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
            if (personalUser == null){
                throw new PersonalNotFoundException("Personal User Not Found");
            }
            PersonalUserDTO personalUserDTO = convertToPersonalUserDTO(user, personalUser);
            resultJson.put("personalCareer", personalUserDTO.getPersonalCareer());
            resultJson.put("isPersonalVisible", personalUserDTO.isPersonalVisible());
            resultJson.put("isCompanyVisible", personalUserDTO.isCompanyVisible());
        }
        // 공통으로 사용하는 소개 추가
        resultJson.put("userIntro",user.getUserIntro());

        return resultJson;
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
     * @param payload
     * @return
     */
    @Override
    public String updateCompanyUser(Long userId, Map<String, Object> payload, MultipartFile logoFile) {
        User user = getUserById(userId);
        CompanyUser companyUser = getCompanyUserById(userId);

        String userName = (String) payload.get("userName");
        String userIntro = (String) payload.get("userIntro");
        String companyAddr = (String) payload.get("companyAddr");
        String currentPassword = (String) payload.get("currentPassword");
        String changePassword = (String) payload.get("changePassword");

        log.info("userName : {}", userName);
        log.info("userIntro  : {}", userIntro);
        log.info("companyAddr  : {}", companyAddr);
        log.info("logoFile : {}", logoFile);
        log.info("currentPassword : {}", currentPassword);
        log.info("changePassword : {}", changePassword);

        if (currentPassword != null) {
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }
        } else {
            throw new InvalidPasswordException("Current password is null");
        }

        String hashedPassword = user.getPassword();
        if (changePassword != null && !changePassword.isEmpty()) {
            hashedPassword = hashPassword(changePassword);
        }

        String bucketName = "ziczone-bucket-jangindle-optimizer";
        String folderName = "CompanyLogo";
        String companyLogoUrl = companyUser.getCompanyLogoUrl();
        String companyLogoUuid = companyUser.getCompanyLogoUuid();
        String companyLogoFileName = companyUser.getCompanyLogoFileName();
        log.info("companyLogoFileName :{}", companyLogoFileName);
        log.info("companyLogoUrl : {}", companyLogoUrl);
        log.info("companyLogoUuid : {}", companyLogoUuid);


        log.info("(logoFile == null || logoFile.isEmpty()) && companyLogoUuid != null : {}",(logoFile == null || logoFile.isEmpty()) && companyLogoUuid != null);
        log.info("(logoFile != null && !logoFile.isEmpty()) : {}",logoFile != null && !logoFile.isEmpty());
        // 기존 파일 삭제 로직
        if ((logoFile == null || logoFile.isEmpty()) && companyLogoUuid != null) {
            storageService.deleteFile(bucketName, folderName, companyLogoUuid);
            companyLogoUrl = null;
            companyLogoUuid = null;
            companyLogoFileName = null;
        }

        // 새로운 파일 업로드 로직
        if (logoFile != null && !logoFile.isEmpty()) {
            if (companyLogoUuid != null) {
                storageService.deleteFile(bucketName, folderName, companyLogoUuid);
            }
            Map<String, String> S3uploadData = storageService.uploadFile(logoFile, folderName, bucketName);
            companyLogoUrl = S3uploadData.get("fileUrl");
            companyLogoUuid = S3uploadData.get("fileUUID");
            companyLogoFileName = logoFile.getOriginalFilename();
        }

        CompanyUser updatedCompanyUser = companyUser.toBuilder()
                .companyAddr(companyAddr)
                .companyLogoUrl(companyLogoUrl)
                .companyLogoUuid(companyLogoUuid)
                .companyLogoFileName(companyLogoFileName)
                .user(user)
                .build();

        user = user.toBuilder()
                .userName(userName != null ? userName : user.getUserName())
                .userIntro(userIntro != null ? userIntro : user.getUserIntro())
                .password(hashedPassword)
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
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            log.warn("User not found with ID: {}", userId);
            return AggregatedDataDTO.builder().build(); // or throw an exception
        }
        log.info("User: {}", user);

        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        if (personalUser == null) {
            log.warn("PersonalUser not found for User ID: {}", userId);
            return AggregatedDataDTO.builder().build(); // or throw an exception
        }
        log.info("Personal User: {}", personalUser);

        // 특정 BuyerId로 모든 SellerId 조회
        List<Long> sellerIds = payHistoryRepository.findSellerIdsByBuyerId(personalUser.getUser().getUserId());
        log.info("Seller IDs: {}", sellerIds);

        // SellerId 리스트로 PersonalUser 조회
        List<PersonalUser> fetchedPersonalUsers = personalUserRepository.findByPersonalIds(sellerIds);
        log.info("Fetched Personal Users: {}", fetchedPersonalUsers);

        // PersonalUser를 PersonalUserDTO로 변환
        List<PersonalUserDTO> personalUsers = fetchedPersonalUsers.stream()
                .map(this::convertToPersonalUserDTO)
                .collect(Collectors.toList());
        log.info("Personal User DTOs: {}", personalUsers);

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

                    CompanyUserDTO companyUserDTO = CompanyUserDTO.builder()
                            .companyId(companyUser.getCompanyId())
                            .build();

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
                            .companyId(companyUser.getCompanyId())
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
                            .commModify(LocalDateTime.now())
                            .userName(user.getUserName())
                            .personalCareer(personalUser.getPersonalCareer())
                            .corrId(board.getCorrId())
                            .corrPoint(board.getCorrPoint())
                            .build();
                })
                .collect(Collectors.toList());
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
                            .companyLogoUrl(companyUser.getCompanyLogoUrl())
                            .companyLogoFileName(companyUser.getCompanyLogoFileName())
                            .companyLogoUuid(companyUser.getCompanyLogoUuid())
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
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$";
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
                .companyLogoUrl(companyUser.getCompanyLogoUrl())
                .companyLogoUuid(companyUser.getCompanyLogoUuid())
                .companyLogoFileName(companyUser.getCompanyLogoFileName())
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
