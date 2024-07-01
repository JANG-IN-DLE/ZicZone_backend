package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.pick.PickCardDTO;
import org.zerock.ziczone.dto.pick.PickJobDTO;
import org.zerock.ziczone.dto.pick.PickResumeDTO;
import org.zerock.ziczone.service.pick.PickService;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class PickController {

    private final PickService pickService;
//  pickzone card 회원들 정보 가져오기
    @GetMapping("/api/pickcards")
    public List<PickCardDTO> getPickCards() {
        return pickService.getPickCards();
    }
    // pickzone 해시태그에 들어가는 정보 가져오기
    @GetMapping("/api/jobs")
    public List<PickJobDTO> getPickJobs() {
        return pickService.getAllJobs();
    }
    // personalId가지고 해당하는 회원 정보 가져오기(pickDetail)
    @GetMapping("/api/pickcards/{personalId}")
    public PickCardDTO getPickCardsByPersonalId(@PathVariable Long personalId) {
        return pickService.getPickCardsById(personalId);
    }
    // personalId가지고 해당하는 회원 resume 정보 가져오기(pickDetail)
    @GetMapping("/api/pickresume/{personalId}")
    public PickResumeDTO getPickResumeByPersonalId(@PathVariable Long personalId) {
        return pickService.getResumeById(personalId);
    }


}
