package org.zerock.ziczone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.mypage.*;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;
import org.zerock.ziczone.service.help.BoardService;
import org.zerock.ziczone.service.help.CommentService;
import org.zerock.ziczone.service.myPage.MyPageService;
import org.zerock.ziczone.service.myPage.MyPageServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService mypageService;
    private final BoardService boardService;



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

    /**
     * 비밀번호 확인 요청
     * @param userId
     * @param json
     * @return ResponseEntity<PersonalUser OR CompanyUser>
     */
    @PostMapping("/user/pw/{userId}")
    public ResponseEntity<Map<String, Object>> getPasswordCheck(@PathVariable Long userId,
                                                                @RequestBody Map<String, Object> json
//    ,@RequestHeader("Authorization") String authorizationHeader
    ) {
        Map<String, Object> result = mypageService.PasswordCheck(userId, json);
        return ResponseEntity.ok(result);
    }

    /**
     * 기업 회원 정보 수정
     *
     * @param @RequestBody  companyUserUpdateDTO
     * @param @PathVariable userId
     * @return ResponseEntity.ok
     */
    @PutMapping("/company/{userId}")
    public ResponseEntity<String> companyUserUpdate(@PathVariable Long userId,
                                                    @RequestPart("payload") String payloadStr,
                                                    @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    ) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Object> payload;
        try {
            payload = objectMapper.readValue(payloadStr, Map.class);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Invalid JSON format in payload");
        }

        return ResponseEntity.ok(mypageService.updateCompanyUser(userId, payload, logoFile));
    }




    /**
     * 개인 유저 정보 조회
     *
     * @param userId 유저 아이디
     * @return ResponseEntity<PersonalUserDTO> 개인 유저 정보
     */
    @GetMapping("/personal/{userId}")
    public ResponseEntity<PersonalUserDTO> getPersonalUserDTO(@PathVariable Long userId) {
        PersonalUserDTO personalUserDTO = mypageService.getPersonalUserDTO(userId);
        return ResponseEntity.ok(personalUserDTO);
    }

    /**
     * 개인 회원 정보 수정
     *
     * @param @RequestBody  personalUserUpdateDTO
     * @param @PathVariable userId
     * @return ResponseEntity.ok
     */
    @PutMapping("/personal/{userId}")
    public ResponseEntity<String> personalUserUpdate(@RequestBody PersonalUserUpdateDTO personalUserUpdateDTO, @PathVariable Long userId) {
        return ResponseEntity.ok(mypageService.updatePersonalUser(userId, personalUserUpdateDTO));
    }




    /**
     * 구매한 이력서 목록 조회
     *
     * @param userId 유저 아이디
     * @return ResponseEntity<List<ResumeDTO>> 구매한 이력서 리스트
     */
    @GetMapping("/personal/purchased/{userId}")
    public ResponseEntity<AggregatedDataDTO> getAggregatedData(@PathVariable Long userId) {
        AggregatedDataDTO aggregatedData = mypageService.getAggregatedData(userId);
        return new ResponseEntity<>(aggregatedData, HttpStatus.OK);
    }


    /**
     * 기업회원 마이페이지 Pick 탭 조회
     * 기업의 픽 탭에는 유저의 이력서를 조회할 수 있는 카드형식의 리스트 데이터를 전송
     * @PathVariable  companyUserId 기업유저 아이디
     * @return ResponseEntity<List<companyUserDTOs>> 기업 공개 설정된 유저 아이디 리스트
     */
    @GetMapping("/company/picks/{userId}")
    public ResponseEntity<List<PersonalUserDTO>> getPicksByCompanyUsersId(@PathVariable Long userId) {
        List<PersonalUserDTO> personalUserDTOs = mypageService.getPicksByCompanyUsers(userId);
        return ResponseEntity.ok(personalUserDTOs);
    }

    /**
     * 개인회원 마이페이지 Pick 탭 조회
     * 유저의 픽 탭에는 기업의 정보를 조회할 수 있는 카드형식의 리스트 데이터를 전송
     * @PathVariable userId 개인유저 아이디
     * @return ResponseEntity<List<PersonalUserDTO>> 개인 유저 정보 리스트
     */
    @GetMapping("/personal/picks/{userId}")
    public ResponseEntity<List<CompanyUserDTO>> getPicksByPersonalUserId(@PathVariable Long userId) {
        List<CompanyUserDTO> companyUserDTOS = mypageService.getPicksByPersonalUsers(userId);

        return ResponseEntity.ok(companyUserDTOS);
    }
    /**
     * 기업회원 마이페이지 scraps 탭 조회
     * 기업의 스크랩 탭에는 기업의 정보를 조회할 수 있는 카드형식의 리스트 데이터를 전송
     * @PathVariable userId 개인유저 아이디
     * @return ResponseEntity<List<PersonalUserDTO>> 개인 유저 정보 리스트
     */
    @GetMapping("/company/scraps/{userId}")
    public ResponseEntity<List<PersonalUserDTO>> getScrapsByPersonalUserId(@PathVariable Long userId) {
        List<PersonalUserDTO> personalUserDTOS = mypageService.getScrapByCompanyUsers(userId);
        return ResponseEntity.ok(personalUserDTOS);
    }

    /**
     * 내가 쓴 게시물 리스트 조회
     * @param userId
     * @return
     */
    @GetMapping("/personal/myboard/{userId}")
    public ResponseEntity<List<BoardDTO>> getBoardUserList(@PathVariable Long userId) {
        List<BoardDTO> boardDTOS = boardService.userReadAll(userId);
        return ResponseEntity.ok(boardDTOS);
    }

    /**
     * 내가 쓴 댓글 게시물 리스트 조회
     * @param userId
     * @return
     */
    @GetMapping("/personal/mycomm/{userId}")
    public ResponseEntity<List<MyCommentListDTO>> getCommentUserList(@PathVariable Long userId) {
        List<MyCommentListDTO> commentDTOS = mypageService.MyCommList(userId);
        return ResponseEntity.ok(commentDTOS);
    }



}
