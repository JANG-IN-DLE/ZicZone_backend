package org.zerock.ziczone.service.myPage;

import org.zerock.ziczone.dto.help.CommentDTO;
import org.zerock.ziczone.dto.mypage.*;

import java.util.List;
import java.util.Optional;

public interface MyPageService {

    // 기업유저 정보 조회
    CompanyUserDTO getCompanyUserDTO(Long userId);
    // 기업유저 정보 업데이트
    String updateCompanyUser(Long userId, CompanyUserUpdateDTO companyUserUpdateDTO);
    // Pick 탭 리스트 조회 (기업 회원)
    List<PersonalUserDTO> getPicksByCompanyUsers(Long userId);
    // Scrap 탭 리스트 조회 (기업 회원)
    List<PersonalUserDTO> getScrapByCompanyUsers(Long userId);


    // 개인유저 정보 조회
    PersonalUserDTO getPersonalUserDTO(Long userId);
    // 개인정보 업데이트
    String updatePersonalUser(Long userId, PersonalUserUpdateDTO personalUserUpdateDTO);
    // 구매한 이력서 목록 조회
    AggregatedDataDTO getAggregatedData(Long userId);
    // Pick 탭 리스트 조회 (개인 회원)
    List<CompanyUserDTO> getPicksByPersonalUsers(Long userId);
    // (커스텀 DTO) 내가 쓴 댓글 조회
    List<MyCommentListDTO> MyCommList(Long userId);
    // 지원서 조회
    ResumeDTO getResume(Long userId);
    // 지원서 수정
    ResumeDTO setResume(Long userId, ResumeDTO resumeDTO);
    // 지원서 삭제[부기능]
//    ResumeDTO deleteResume(Long userId, ResumeDTO resumeDTO);
    // 토스 결제



}
