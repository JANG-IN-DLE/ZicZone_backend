package org.zerock.ziczone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.mypage.*;
import org.zerock.ziczone.exception.mypage.PersonalNotFoundException;
import org.zerock.ziczone.repository.PayHistoryRepository;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.repository.payment.PaymentRepository;
import org.zerock.ziczone.service.help.BoardService;
import org.zerock.ziczone.service.help.CommentService;
import org.zerock.ziczone.service.myPage.MyPageService;
import org.zerock.ziczone.service.myPage.MyPageServiceImpl;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService mypageService;
    private final MyPageServiceImpl myPageServiceImpl;
    private final BoardService boardService;
    private final CommentService commentService;
    private final PaymentRepository paymentRepository;
    private final PersonalUserRepository personalUserRepository;
    private final UserRepository userRepository;
    private final PayHistoryRepository payHistoryRepository;


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
        Map<String, Object> payload;
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
     * 개인 유저의 총 베리 포인트를 반환
     *
     * @param userId 유저 아이디
     * @return ResponseEntity<Map<String, Integer>> 총 베리 포인트
     */
    @GetMapping("/personal/totalBerryPoints/{userId}")
    public ResponseEntity<Map<String, Integer>> getTotalBerryPoints(@PathVariable Long userId) {
        // User ID로 PersonalUser 조회
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);

        // personalId로 성공한 결제들의 베리 포인트 합산
        Integer totalBerryPoints = paymentRepository.findTotalBerryPointsByPersonalId(personalUser.getPersonalId())
                .orElse(0); // Optional이 비어있는 경우 0을 기본값으로 반환

        // personalId로 PayHistory에서 berryBucket 값을 가져와 합산
        List<PayHistory> payHistoryList = payHistoryRepository.findByPersonalUserPersonalId(personalUser.getPersonalId());
        int totalBerryBucket = payHistoryList.stream()
                .mapToInt(payHistory -> Integer.parseInt(payHistory.getBerryBucket()))
                .sum();

        // 최종 총 베리 포인트 계산
        int finalTotalBerryPoints = totalBerryPoints + totalBerryBucket;

        // 결과를 맵에 담아 반환
        Map<String, Integer> response = new HashMap<>();
        response.put("totalBerryPoints", finalTotalBerryPoints);

        return ResponseEntity.ok(response);
    }



    /**
     * 포인트 사용 내역 리스트
     *
     * @param userId 유저 아이디
     * @return ResponseEntity<PersonalUserPointDTO> 남은 포인트 정보
     */
    @PostMapping("/personal/points/{userId}")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getPersonalUserRemainingPoints(@PathVariable Long userId) {
        User user = userRepository.findByUserId(userId);

        log.info("user : {} ", user);

        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        log.info("personalUser : {}", personalUser);

        Optional<List<Payment>> paymentsOptional = paymentRepository.findAllSuccessfulPaymentsByPersonalId(personalUser.getPersonalId());
        List<Map<String, String>> paymentDetailsList = new ArrayList<>();
        if (paymentsOptional.isEmpty() || paymentsOptional.get().isEmpty()) {
            log.info("No successful payments found for personalId: {}", personalUser.getPersonalId());
        } else {
            List<Payment> payments = paymentsOptional.get();
            log.info("Payments : {}", payments);
            paymentDetailsList = payments.stream().map(payment -> {
                Map<String, String> paymentDetails = new HashMap<>();
                paymentDetails.put("payId", payment.getPayId().toString());
                paymentDetails.put("payState", payment.getPayState().name());
                paymentDetails.put("amount", payment.getAmount().toString());
                paymentDetails.put("payDate", payment.getPayDate().toString());
                paymentDetails.put("paymentKey", payment.getPaymentKey());
                paymentDetails.put("berryPoint", payment.getBerryPoint().toString());
                paymentDetails.put("orderId", payment.getOrderId());
                return paymentDetails;
            }).collect(Collectors.toList());
        }

        List<PayHistory> payHistoryList = payHistoryRepository.findByPersonalUserPersonalId(personalUser.getPersonalId());
        List<Map<String, String>> payHistoryDetailsList = new ArrayList<>();
        if (payHistoryList.isEmpty()) {
            log.info("No pay history found for personalId: {}", personalUser.getPersonalId());
        } else {
            log.info("payHistoryList : {}", payHistoryList);
            payHistoryDetailsList = payHistoryList.stream().map(payHistory -> {
                Map<String, String> payHistoryDetails = new HashMap<>();
                payHistoryDetails.put("payHistoryId", payHistory.getPayHistoryId().toString());
                payHistoryDetails.put("sellerId", payHistory.getSellerId().toString());
                payHistoryDetails.put("buyerId", payHistory.getBuyerId().toString());
                payHistoryDetails.put("berryBucket", payHistory.getBerryBucket());
                payHistoryDetails.put("payHistoryContent", payHistory.getPayHistoryContent());
                payHistoryDetails.put("payHistoryDate", payHistory.getPayHistoryDate().toString());
                return payHistoryDetails;
            }).collect(Collectors.toList());
        }

        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("payment", paymentDetailsList);
        response.put("payHistory", payHistoryDetailsList);

        return ResponseEntity.ok(response);
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
        log.info(aggregatedData.toString());
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
     * 기업회원 마이페이지 Pick 탭 조회
     * 기업의 픽 탭에는 기업의 정보를 조회할 수 있는 카드형식의 리스트 데이터를 전송
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
