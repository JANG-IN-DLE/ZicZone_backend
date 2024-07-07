package org.zerock.ziczone.service.myPage;

import org.zerock.ziczone.dto.mypage.*;

import java.util.List;
import java.util.Optional;

public interface MyPageService {

    // 기업유저 정보 조회
    CompanyUserDTO getCompanyUserDTO(Long userId);
    // 기업유저 정보 업데이트
    CompanyUserDTO updateCompanyUser(Long userId, CompanyUserUpdateDTO companyUserUpdateDTO);

    // 개인유저 정보 조회
    PersonalUserDTO getPersonalUserDTO(Long userId);
    // 개인정보 업데이트
    PersonalUserDTO updatePersonalUser(Long userId, PersonalUserUpdateDTO personalUserUpdateDTO);

    // 개인 공개 설정 유저 아이디 리스트 조회
    List<Long> getVisiblePersonalIds();

    // 기업 공개 설정 유저 아이디 리스트 조회
    List<Long> getVisibleCompanyIds();

    // 구매한 이력서 목록 조회
    AggregatedDataDTO getAggregatedData(Long userId);

    // Pick 탭 기업정보 리스트 조회
    List<PersonalUserDTO> getPicksByCompanyUsers(Long personalUserId);

    // Pick 탭 개인정보 조회
    List<CompanyUserDTO> getPicksByPersonalUsers(Long personalUserId);
}
