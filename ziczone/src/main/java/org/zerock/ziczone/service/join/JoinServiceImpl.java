package org.zerock.ziczone.service.join;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.job.Job;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.member.UserType;
import org.zerock.ziczone.domain.tech.Tech;
import org.zerock.ziczone.domain.tech.TechStack;
import org.zerock.ziczone.dto.join.CompanyUserDTO;
import org.zerock.ziczone.dto.join.PersonalUserDTO;
import org.zerock.ziczone.dto.join.TechDTO;
import org.zerock.ziczone.repository.job.JobPositionRepository;
import org.zerock.ziczone.repository.job.JobRepository;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.tech.TechRepository;
import org.zerock.ziczone.repository.tech.TechStackRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class JoinServiceImpl implements JoinService {

    private final UserRepository userRepository;
    private final PersonalUserRepository personalUserRepository;
    private final CompanyUserRepository companyUserRepository;

    private final JobPositionRepository jobPositionRepository;
    private final TechStackRepository techStackRepository;
    private final JobRepository jobRepository;
    private final TechRepository techRepository;

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
    public String personalSignUp(PersonalUserDTO personalUserDTO) {

        //회원
        User user = User.builder()
                .userName(personalUserDTO.getUserName())
                .email(personalUserDTO.getEmail())
                .password(personalUserDTO.getPassword())
                .userType(UserType.PERSONAL)
                .userIntro(personalUserDTO.getUserIntro())
                .userCreate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        //개인회원
        PersonalUser personalUser = PersonalUser.builder()
                .user(user)
                .personalCareer(personalUserDTO.getPersonalCareer())
                .gender(personalUserDTO.getGender())
                .build();
        personalUserRepository.save(personalUser);

        //희망직무
        for (Long jobId : personalUserDTO.getJobIds()) {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("없는 직무입니다."));
            JobPosition jobPosition = JobPosition.builder()
                    .personalUser(personalUser)
                    .job(job)
                    .build();
            jobPositionRepository.save(jobPosition);
        }

        // 기술 스택 정보 저장
        for (Long techId : personalUserDTO.getTechIds()) {
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

    @Override
    public String companyJoin(CompanyUserDTO companyUserDTO) {
        //회원
        User user = User.builder()
                .userName(companyUserDTO.getUserName())
                .email(companyUserDTO.getEmail())
                .password(companyUserDTO.getPassword())
                .userType(UserType.COMPANY)
                .userIntro(companyUserDTO.getUserIntro())
                .userCreate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        //기업회원
        CompanyUser companyUser = CompanyUser.builder()
                .user(user)
                .companyNum(companyUserDTO.getCompanyNum())
                .companyAddr(companyUserDTO.getCompanyAddr())
                .companyYear(companyUserDTO.getCompanyYear())
                .companyLogo("companyUserDTO.getCompanyLogo()")
                .companyCeo(companyUserDTO.getCompanyCeo())
                .build();
        companyUserRepository.save(companyUser);

        return "signUp success";
    }

    //해당 이메일을 가진 유저가 있는지 검사
    @Override
    public User EmailDuplication(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

}
