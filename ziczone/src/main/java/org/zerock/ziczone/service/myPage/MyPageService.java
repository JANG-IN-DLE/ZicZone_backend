package org.zerock.ziczone.service.myPage;

import org.zerock.ziczone.dto.mypage.CompanyUserDTO;
import org.zerock.ziczone.dto.mypage.PersonalUserDTO;
import org.zerock.ziczone.dto.mypage.PersonalUserPointDTO;
import org.zerock.ziczone.dto.mypage.ResumeDTO;

import java.util.List;
import java.util.Optional;

public interface MyPageService {

    CompanyUserDTO getCompanyUserDTO(Long companyId);
    PersonalUserDTO getPersonalUserDTO(Long userId);
    Optional<PersonalUserPointDTO> getPersonalUserRemainingPoints(Long userId);
    List<ResumeDTO> getPurchasedResumes(Long userId);
    List<Long> getVisiblePersonalIds();
    List<Long> getVisibleCompanyIds();

    List<PersonalUserDTO> getPicksByCompanyUsers(Long userId);
    // 주어진 개인 사용자 ID에 대한 모든 pick 항목을 가져옴
    List<CompanyUserDTO> getPicksByPersonalUsers(Long personalUserId);
}
