package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.ziczone.dto.join.TechDTO;
import org.zerock.ziczone.service.join.JoinService;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @GetMapping("/techs")
    public List<TechDTO> techs() { return joinService.getAllTechs(); }
}
