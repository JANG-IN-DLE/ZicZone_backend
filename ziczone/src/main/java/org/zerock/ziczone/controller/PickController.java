package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.ziczone.dto.pick.PickCardDTO;
import org.zerock.ziczone.service.pick.PickService;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class PickController {
    private final PickService pickService;

    @GetMapping("/pick")
    public String getPickCards(Model model) {
        List<PickCardDTO> pickCards = pickService.getPickCards();
        model.addAttribute("pickCards", pickCards);
        return "pickcards";
    }
}
