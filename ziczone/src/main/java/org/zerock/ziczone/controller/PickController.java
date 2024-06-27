package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.ziczone.dto.pick.PickCardDTO;
import org.zerock.ziczone.dto.pick.PickJobDTO;
import org.zerock.ziczone.service.pick.PickService;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class PickController {

    private final PickService pickService;

    @GetMapping("/api/pickcards")
    public List<PickCardDTO> getPickCards() {
        return pickService.getPickCards();
    }
    @GetMapping("/api/jobs")
    public List<PickJobDTO> getPickJobs() {
        return pickService.getAllJobs();
    }


}
