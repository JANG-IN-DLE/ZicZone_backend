package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.pick.*;
import org.zerock.ziczone.service.pick.PickService;

import java.net.URI;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class PickController {

    private final PickService pickService;
//  pickzone card 회원들 정보 가져오기
    @GetMapping("/api/pickcards")
    public List<PickCardDTO> getPickCards(@RequestParam Long loggedInPersonalId) {
        return pickService.getPickCards(loggedInPersonalId);
    }
//    // pickzone card company로 로그인했을 때
//    @GetMapping("/api/pickcards/{companyId}")
//    public List<PickCardDTO> getPickCards(){
//        return pickService.getPickCards
//    }
    // pickzone 해시태그에 들어가는 정보 가져오기
    @GetMapping("/api/jobs")
    public List<PickJobDTO> getPickJobs() {
        return pickService.getAllJobs();
    }
    // (CompanyId로 로그인되어을때) personalId가지고 해당하는 회원 정보 가져오기(pickDetail)
    @GetMapping("/api/pickcards/{companyId}/{personalId}")
    public PickDetailDTO getPickCardsByCompanyId(@PathVariable Long companyId, @PathVariable Long personalId) {
        return pickService.getPickCardsById(companyId, personalId);
    }
    // (PersonalId로 로그인되었을때) personalId가지고 해당하는 회원 정보 가져오기(pickDetail)
    @GetMapping("/api/pickcards/personal/{loggedInPersonalId}/{personalId}")
    public PickPersonalDetailDTO getPickCardsByPersonalId(@PathVariable Long loggedInPersonalId , @PathVariable Long personalId) {
        return pickService.getPickCardsByPersonalId(loggedInPersonalId, personalId);
    }
    // personalId가지고 해당하는 회원 resume 정보 가져오기(pickDetail)
    @GetMapping("/api/pickresume/{personalId}")
    public PickResumeDTO getPickResumeByPersonalId(@PathVariable Long personalId) {
        return pickService.getResumeById(personalId);
    }
    // pickzone에서 card 오픈하려고 할때 처리하는 메서드
    @PostMapping("/api/open-card")
    public ResponseEntity<?> openCard(@RequestBody OpenCardDTO openCardDTO){
        try{
            boolean alreadyPaid = pickService.handlePayment(openCardDTO);
            if(alreadyPaid){
                // 이미 결제가 존재하는 경우 /pickzone/:personalId로 리다이렉트
                // GetMapping("/api/pickcards/{personalId}")이 URI로 받아야한다.
                URI location = URI.create("/api/pickcards/personal/" + openCardDTO.getBuyerId() + "/" + openCardDTO.getSellerId());
                return ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();
            }
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // scrap 요청을 처리하는 메서드
    @PostMapping("/api/scrap")
    public ResponseEntity<?> scrapUser(@RequestBody PickAndScrapDTO pickAndScrapDTO){
        PickAndScrapDTO updatedPickAndScrapDTO = pickService.scrapUser(pickAndScrapDTO);
        return ResponseEntity.ok(updatedPickAndScrapDTO);
    }
    // pick 요청을 처리하는 메서드
    @PostMapping("/api/pick")
    public ResponseEntity<?> pickUser(@RequestBody PickAndScrapDTO pickAndScrapDTO){
        PickAndScrapDTO updatedPickAndScrapDTO = pickService.pickUser(pickAndScrapDTO);
        return ResponseEntity.ok(updatedPickAndScrapDTO);
    }
}
