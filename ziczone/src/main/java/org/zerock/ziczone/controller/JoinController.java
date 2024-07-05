package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.join.CompanyUserDTO;
import org.zerock.ziczone.dto.join.PersonalUserDTO;
import org.zerock.ziczone.dto.join.TechDTO;
import org.zerock.ziczone.service.join.JoinService;

import java.util.List;
import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @GetMapping("/techs")
    public List<TechDTO> techs() { return joinService.getAllTechs(); }

    @PostMapping("/personal")
    public ResponseEntity<String> personalUserSignup(@RequestBody PersonalUserDTO personalUserDTO) {
        String SignUpSuccess = joinService.personalSignUp(personalUserDTO);

        if(Objects.equals(SignUpSuccess, "signUp success")) {
            return ResponseEntity.ok("Personal user signup successful");
        }else {
            return ResponseEntity.ok("Personal user signup failed");
        }
    }

    @PostMapping("/company")
    public ResponseEntity<String> companyUserSignUp(@RequestBody CompanyUserDTO companyUserDTO) {
        String SignUpSuccess = joinService.companyJoin(companyUserDTO);

        if(Objects.equals(SignUpSuccess, "signUp success")) {
            return ResponseEntity.ok("Company user signup successful");
        }else {
            return ResponseEntity.ok("Company user signup failed");
        }
    }
}
