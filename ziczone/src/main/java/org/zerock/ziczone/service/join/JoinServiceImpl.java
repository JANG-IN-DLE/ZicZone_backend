package org.zerock.ziczone.service.join;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.job.Job;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.member.UserType;
import org.zerock.ziczone.domain.tech.Tech;
import org.zerock.ziczone.domain.tech.TechStack;
import org.zerock.ziczone.dto.join.CompanyUserJoinDTO;
import org.zerock.ziczone.dto.join.PersonalUserJoinDTO;
import org.zerock.ziczone.dto.join.TechDTO;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.job.JobRepository;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.tech.TechRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;
import org.zerock.ziczone.service.storage.StorageService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class JoinServiceImpl implements JoinService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final PersonalUserRepository personalUserRepository;
    private final CompanyUserRepository companyUserRepository;

    private final JobPositionRepository jobPositionRepository;
    private final TechStackRepository techStackRepository;
    private final JobRepository jobRepository;
    private final TechRepository techRepository;

    private final StorageService storageService;;

    //회원가입 stack가져오는 메소드
    @Override
    public List<TechDTO> getAllTechs() {
        List<Tech> techs = techRepository.findAll();
        return techs.stream()
                .map(tech -> TechDTO.builder()
                        .techId(tech.getTechId())
                        .techName(tech.getTechName())
                        .techUrl(tech.getTechUrl())
                        .build())
                .collect(Collectors.toList());

    }

    //개인회원가입
    @Override
    public String personalSignUp(PersonalUserJoinDTO personalUserJoinDTO) {

        //회원
        User user = User.builder()
                .userName(personalUserJoinDTO.getUserName())
                .email(personalUserJoinDTO.getEmail())
                .password(passwordEncoder.encode(personalUserJoinDTO.getPassword()))
                .userType(UserType.PERSONAL)
                .userIntro(personalUserJoinDTO.getUserIntro())
                .userCreate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        //개인회원
        PersonalUser personalUser = PersonalUser.builder()
                .user(user)
                .personalCareer(personalUserJoinDTO.getPersonalCareer())
                .gender(personalUserJoinDTO.getGender())
                .build();
        personalUserRepository.save(personalUser);

        //희망직무
        for (Long jobId : personalUserJoinDTO.getJobIds()) {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("없는 직무입니다."));
            JobPosition jobPosition = JobPosition.builder()
                    .personalUser(personalUser)
                    .job(job)
                    .build();
            jobPositionRepository.save(jobPosition);
        }

        // 기술 스택 정보 저장
        for (Long techId : personalUserJoinDTO.getTechIds()) {
            Tech tech = techRepository.findById(techId)
                    .orElseThrow(() -> new RuntimeException("없는 스택입니다."));
            TechStack techStack = TechStack.builder()
                    .personalUser(personalUser)
                    .tech(tech)
                    .build();
            techStackRepository.save(techStack);
        }

        return "signUp success";
    }

    public CompanyUserJoinDTO JsonToDTO(String json) {
        // companyUserDTOJson을 CompanyUserDTO 객체로 변환
        CompanyUserJoinDTO companyUserJoinDTO;
        try {
            companyUserJoinDTO = new ObjectMapper().readValue(json, CompanyUserJoinDTO.class);
            return companyUserJoinDTO;
        } catch (IOException e) {
            log.error("Failed to parse companyUserDTO", e);
            return null;
        }
    }

    @Override
    public String companySignUp(MultipartFile companyLogoFile, String companyUserDTOJson) {

        //json -> dto
        CompanyUserJoinDTO companyUserJoinDTO = JsonToDTO(companyUserDTOJson);

        // 설립날짜 String -> LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String uuid = UUID.randomUUID().toString(); //uuid
        String bucketName = "ziczone-bucket-jangindle-optimizer";
        String folderName = "CompanyLogo";
        String companyLogoURL = storageService.uploadFile(companyLogoFile, folderName, uuid, bucketName);

        //회원
        User user = User.builder()
                .userName(companyUserJoinDTO.getUserName())
                .email(companyUserJoinDTO.getEmail())
                .password(passwordEncoder.encode(companyUserJoinDTO.getPassword()))
                .userType(UserType.COMPANY)
                .userIntro(companyUserJoinDTO.getUserIntro())
                .userCreate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        //기업회원
        CompanyUser companyUser = CompanyUser.builder()
                .user(user)
                .companyNum(companyUserJoinDTO.getCompanyNum())
                .companyAddr(companyUserJoinDTO.getCompanyAddr())
                .companyYear(LocalDate.parse(companyUserJoinDTO.getCompanyYear(), formatter))
                .companyLogoFileName(companyLogoFile.getOriginalFilename())
                .companyLogoUUID(uuid)
                .companyLogoUrl(companyLogoURL)
                .companyCeo(companyUserJoinDTO.getCompanyCeo())
                .build();
        companyUserRepository.save(companyUser);

        return "signUp success";
    }

    //해당 이메일을 가진 유저가 있는지 검사
    @Override
    public User EmailDuplication(String email) {

        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

}
