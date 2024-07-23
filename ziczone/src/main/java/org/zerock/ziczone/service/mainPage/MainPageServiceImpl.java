package org.zerock.ziczone.service.mainPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.join.CompanyUserJoinDTO;
import org.zerock.ziczone.dto.join.PersonalUserJoinDTO;
import org.zerock.ziczone.dto.main.MainCompanyUserDTO;
import org.zerock.ziczone.dto.main.MainPersonalUserDTO;
import org.zerock.ziczone.repository.member.CompanyUserRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainPageServiceImpl implements MainPageService {
    private final PaymentRepository paymentRepository;

    private final CompanyUserRepository companyUserRepository;
    private final PersonalUserRepository personalUserRepository;
    private final UserRepository userRepository;

    @Override
    public List<String> companyLogoList() {
        return companyUserRepository.findAllCompanyLogo();
    }

    @Override
    public MainCompanyUserDTO getCompanyUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        CompanyUser companyUser = companyUserRepository.findByUser_UserId(user.getUserId());
        return MainCompanyUserDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .userIntro(user.getUserIntro())
                .companyLogo(companyUser.getCompanyLogoUrl())
                .build();
    }

    @Override
    public MainPersonalUserDTO getPersonalUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(user.getUserId());
        Payment payment = paymentRepository.findByPersonalUser(personalUser);

        return MainPersonalUserDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .gender(personalUser.getGender())
                .userIntro(user.getUserIntro())
                .personalCareer(personalUser.getPersonalCareer())
                .build();
    }
}
