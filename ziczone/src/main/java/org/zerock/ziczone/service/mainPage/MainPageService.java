package org.zerock.ziczone.service.mainPage;

import org.zerock.ziczone.dto.join.CompanyUserJoinDTO;
import org.zerock.ziczone.dto.join.PersonalUserJoinDTO;

import java.util.List;

public interface MainPageService {
    List<String> companyLogoList();

    //메인페이지 프로필카드 정보(기업)
    CompanyUserJoinDTO getCompanyUser(Long userId);

    //메인페이지 프로필카드 정보(개인)
    PersonalUserJoinDTO getPersonalUser(Long userId);
}
