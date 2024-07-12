package org.zerock.ziczone.service.mainPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.join.CompanyUserJoinDTO;
import org.zerock.ziczone.dto.join.PersonalUserJoinDTO;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainPageServiceImpl implements MainPageService {

    private final CompanyUserRepository companyUserRepository;
    private final PersonalUserRepository personalUserRepository;
    private final UserRepository userRepository;

    @Override
    public List<String> companyLogoList() {
        return companyUserRepository.findAllCompanyLogo();
    }

    @Override
    public CompanyUserJoinDTO getCompanyUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        CompanyUser companyUser = companyUserRepository.findByUser_UserId(user.getUserId());
        return CompanyUserJoinDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .companyLogo(companyUser.getCompanyLogo())
                .userType(null)
                .password(null)
                .userIntro(null)
                .companyNum(null)
                .companyAddr(null)
                .companyYear(null)
                .companyCeo(null)
                .userCreate(null)
                .build();
    }

    @Override
    public PersonalUserJoinDTO getPersonalUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(user.getUserId());
        return PersonalUserJoinDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .gender(personalUser.getGender())
                .password(null)
                .userType(null)
                .userIntro(null)
                .personalCareer(null)
                .jobIds(null)
                .techIds(null)
                .build();
    }
}
