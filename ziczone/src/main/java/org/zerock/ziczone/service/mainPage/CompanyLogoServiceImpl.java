package org.zerock.ziczone.service.mainPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zerock.ziczone.repository.member.CompanyUserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyLogoServiceImpl implements CompanyLogoService {

    private final CompanyUserRepository companyUserRepository;

    @Override
    public List<String> companyLogoList() {
        return companyUserRepository.findAllCompanyLogo();
    }
}
