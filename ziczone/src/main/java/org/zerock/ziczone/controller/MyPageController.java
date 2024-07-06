package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.dto.mypage.*;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.service.myPage.MyPageService;
import org.zerock.ziczone.service.myPage.MyPageServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService mypageService;
    private final MyPageServiceImpl myPageServiceImpl;

    /**
     * 기업 유저 정보 조회
     *
     * @param userId 유저 아이디
     * @return ResponseEntity<CompanyUserDTO> 기업 유저 정보
     */
    @GetMapping("/company/{userId}")
    public ResponseEntity<CompanyUserDTO> getCompanyUserDTO(@PathVariable Long userId) {
        CompanyUserDTO companyUserDTO = mypageService.getCompanyUserDTO(userId);
        return ResponseEntity.ok(companyUserDTO);
    }

    
    @PutMapping("/companyUser/{userId}")
    public ResponseEntity<CompanyUserDTO> updateCompanyUser(
            @PathVariable Long userId,
            @RequestBody CompanyUserUpdateDTO companyUserUpdateDTO) {
        return ResponseEntity.ok(mypageService.updateCompanyUser(userId, companyUserUpdateDTO));
    }

    /**
     * 개인 유저 정보 조회
     *
     * @param userId 유저 아이디
     * @return ResponseEntity<PersonalUserDTO> 개인 유저 정보
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PersonalUserDTO> getPersonalUserDTO(@PathVariable Long userId) {
        PersonalUserDTO personalUserDTO = mypageService.getPersonalUserDTO(userId);
        return ResponseEntity.ok(personalUserDTO);
    }

    @PutMapping("/personalUser/{userId}")
    public ResponseEntity<PersonalUserDTO> updatePersonalUser(
            @PathVariable Long userId,
            @RequestBody PersonalUserUpdateDTO personalUserUpdateDTO) {
        return ResponseEntity.ok(mypageService.updatePersonalUser(userId, personalUserUpdateDTO));
    }

//    /**
//     * 남은 포인트 조회 사용불가 업데이트 예정
//     *
//     * @param userId 유저 아이디
//     * @return ResponseEntity<PersonalUserPointDTO> 남은 포인트 정보
//     */
//    @GetMapping("/{userId}/points")
//    public ResponseEntity<PersonalUserPointDTO> getPersonalUserRemainingPoints(@PathVariable Long userId) {
//        return mypageService.getPersonalUserRemainingPoints(userId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }


    /**
     * 구매한 이력서 목록 조회
     *
     * @param personalId 유저 아이디
     * @return ResponseEntity<List<ResumeDTO>> 구매한 이력서 리스트
     */
    @GetMapping("/purchased/{personalId}")
    public ResponseEntity<AggregatedDataDTO> getAggregatedData(@PathVariable Long personalId) {
        AggregatedDataDTO aggregatedData = mypageService.getAggregatedData(personalId);
        log.info(aggregatedData.toString());
        return new ResponseEntity<>(aggregatedData, HttpStatus.OK);
    }


    /**
     * 개인 공개 설정된 유저 아이디 리스트 조회
     *
     * @return ResponseEntity<List<Long>> 개인 공개 설정된 유저 아이디 리스트
     */
    @GetMapping("/visible-user")
    public ResponseEntity<List<Long>> getVisiblePersonalIds() {
        List<Long> visiblePersonalIds = mypageService.getVisiblePersonalIds();
        return ResponseEntity.ok(visiblePersonalIds);
    }

    /**
     * 기업 공개 설정된 유저 아이디 리스트 조회
     *
     * @return List<Long>
     */
    @GetMapping("/visible-company")
    public ResponseEntity<List<Long>> getVisibleCompanyIds() {
        List<Long> visibleCompanyIds = mypageService.getVisibleCompanyIds();
        return ResponseEntity.ok(visibleCompanyIds);
    }


    /**
     * 개인회원 마이페이지 Pick 탭 조회
     * 기업의 픽 탭에는 유저의 이력서를 조회할 수 있는 카드형식의 리스트 데이터를 전송
     * @PathVariable  companyUserId 기업유저 아이디
     * @return ResponseEntity<List<companyUserDTOs>> 기업 공개 설정된 유저 아이디 리스트
     */
    @GetMapping("/company-user-picks/{companyUserId}")
    public ResponseEntity<List<PersonalUserDTO>> getPicksByCompanyUsersId(@PathVariable Long companyUserId) {
        List<PersonalUserDTO> personalUserDTOs = mypageService.getPicksByCompanyUsers(companyUserId);
        return ResponseEntity.ok(personalUserDTOs);
    }

    /**
     * 기업회원 마이페이지 Pick 탭 조회
     * 유저의 픽 탭에는 기업의 정보를 조회할 수 있는 카드형식의 리스트 데이터를 전송
     * @PathVariable personalUserId 개인유저 아이디
     * @return ResponseEntity<List<PersonalUserDTO>> 개인 유저 정보 리스트
     */
    @GetMapping("/personal-user-picks/{personalUserId}")
    public ResponseEntity<List<CompanyUserDTO>> getPicksByPersonalUserId(@PathVariable Long personalUserId) {
        List<CompanyUserDTO> companyUserDTOS = mypageService.getPicksByPersonalUsers(personalUserId);

        return ResponseEntity.ok(companyUserDTOS);
    }

}