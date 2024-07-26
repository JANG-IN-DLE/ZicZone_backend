package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.pick.*;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.service.alarm.AlarmServiceImpl;
import org.zerock.ziczone.service.pick.PickService;

import java.net.URI;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class PickController {

    private final PickService pickService;
    private final AlarmServiceImpl alarmServiceImpl;
    private final PersonalUserRepository personalUserRepository;
    private final UserRepository userRepository;

    // main페이지에서 로그인 안된 회원들을 위한 Get요청
    @GetMapping("/api/pickcards")
    public List<PickCardDTO> getPickCards() {
        return pickService.getPickCards();
    }
    
//  (personal회원이 로그인한 경우) pickzone card 회원들 정보 가져오기
    @GetMapping("/api/personal/pickcards")
    public List<PickCardDTO> getPersonalPickCards(@RequestParam Long loggedInUserId) {
        return pickService.getPersonalPickCards(loggedInUserId);
    }
//    (company회원이 로그인한 경우) pickzone card 회원들 정보 가져오기
    @GetMapping("/api/company/pickcards")
    public List<PickCardDTO> getCompanyPickCards(@RequestParam Long loggedInUserId){
        return pickService.getCompanyPickCards(loggedInUserId);
    }
    // pickzone 해시태그에 들어가는 정보 가져오기
    @GetMapping("/api/jobs")
    public List<PickJobDTO> getPickJobs() {
        return pickService.getAllJobs();
    }
    // (CompanyId로 로그인되어을때) personalId가지고 해당하는 회원 정보 가져오기(pickDetail  왼쪽 회원 정보)
    @GetMapping("/api/company/pickcards/{loggedInUserId}/{personalId}")
    public PickDetailDTO getPickCardsByCompanyId(@PathVariable Long loggedInUserId, @PathVariable Long personalId) {
        return pickService.getPickCardsById(loggedInUserId, personalId);
    }
    // (PersonalId로 로그인되었을때) personalId가지고 해당하는 회원 정보 가져오기(pickDetail 왼쪽 회원정보)
    @GetMapping("/api/personal/pickcards/{loggedInUserId}/{personalId}")
    public PickPersonalDetailDTO getPickCardsByPersonalId(@PathVariable Long loggedInUserId , @PathVariable Long personalId) {
        return pickService.getPickCardsByPersonalId(loggedInUserId, personalId);
    }
    // (CompanyId로 로그인되었을때) personalId가지고 해당하는 회원 resume 정보 가져오기(pickDetail 오른쪽 정보)
    @GetMapping("/api/company/pickresume/{personalId}")
    public PickResumeDTO getCompanyPickResumeByPersonalId(@PathVariable Long personalId) {
        return pickService.getResumeById(personalId);
    }
    // (PersonalId로 로그인되었을때) personalId가지고 해당하는 회원 resume 정보 가져오기(pickDetail 오른쪽 정보)
    @GetMapping("/api/personal/pickresume/{personalId}")
    public PickResumeDTO getPersonalPickResumeByPersonalId(@PathVariable Long personalId) {
        return pickService.getResumeById(personalId);
    }

    // (PersonalId로 로그인되었을때) pickzone에서 card 오픈하려고 할때 처리하는 메서드(buyerId는 로그인userId, sellerId는 personalId)
    @PostMapping("/api/personal/open-card")
    public ResponseEntity<?> openCard(@RequestBody OpenCardDTO openCardDTO){
        try{
            boolean alreadyPaid = pickService.handlePayment(openCardDTO);
            if(alreadyPaid) {
                // 이미 결제가 존재하는 경우 /pickzone/:personalId로 리다이렉트
                // GetMapping("/api/pickcards/{personalId}")이 URI로 받아야한다.
                URI location = URI.create("/api/personal/pickcards/" + openCardDTO.getBuyerId() + "/" + openCardDTO.getSellerId());
                return ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();
            }
            PersonalUser sellerUser = personalUserRepository.findByPersonalId(openCardDTO.getSellerId());
            alarmServiceImpl.addAlarm("BUYRESUME", openCardDTO.getBuyerId(), sellerUser.getUser().getUserId());
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // (CompanyId로 로그인되었을때) scrap 요청을 처리하는 메서드 (userId 로그인된 유저, personalId 당한 유저 personalId)
    @PostMapping("/api/company/scrap")
    public ResponseEntity<?> scrapUser(@RequestBody PickAndScrapDTO pickAndScrapDTO){
        PickAndScrapDTO updatedPickAndScrapDTO = pickService.scrapUser(pickAndScrapDTO);
        PersonalUser scrapedUser = personalUserRepository.findByPersonalId(updatedPickAndScrapDTO.getPersonalId());
        alarmServiceImpl.addAlarm("SCRAP", updatedPickAndScrapDTO.getUserId(), scrapedUser.getUser().getUserId());
        return ResponseEntity.ok(updatedPickAndScrapDTO);
    }
    // (CompanyId로 로그인되었을때) pick 요청을 처리하는 메서드
    @PostMapping("/api/company/pick")
    public ResponseEntity<?> pickUser(@RequestBody PickAndScrapDTO pickAndScrapDTO){
        PickAndScrapDTO updatedPickAndScrapDTO = pickService.pickUser(pickAndScrapDTO);
        PersonalUser pickedUser = personalUserRepository.findByPersonalId(updatedPickAndScrapDTO.getPersonalId());
        alarmServiceImpl.addAlarm("PICK", updatedPickAndScrapDTO.getUserId(), pickedUser.getUser().getUserId());
        return ResponseEntity.ok(updatedPickAndScrapDTO);
    }
}
