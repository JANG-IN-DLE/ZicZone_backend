package org.zerock.ziczone.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.dto.join.CompanyUserJoinDTO;
import org.zerock.ziczone.dto.join.PersonalUserJoinDTO;
import org.zerock.ziczone.dto.join.TechDTO;
import org.zerock.ziczone.service.join.JoinService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;
    private final AmazonS3 amazonS3;

    @GetMapping("/techs")
    public List<TechDTO> techs() { return joinService.getAllTechs(); }

    @PostMapping("/personal")
    public ResponseEntity<String> personalUserSignup(@RequestBody PersonalUserJoinDTO personalUserJoinDTO) {
        String SignUpSuccess = joinService.personalSignUp(personalUserJoinDTO);

        if(Objects.equals(SignUpSuccess, "signUp success")) {
            return ResponseEntity.ok("Personal user signup successful");
        }else {
            return ResponseEntity.ok("Personal user signup failed");
        }
    }

    @PostMapping(value="/company", consumes = "multipart/form-data")
    public ResponseEntity<String> companyUserSignUp(@RequestParam(value = "companyLogo") MultipartFile companyLogo,
                                                    @RequestParam("companyUserDTO") String companyUserDTOJson) {

        String SignUpSuccess = joinService.companySignUp(companyLogo, companyUserDTOJson);

        if(Objects.equals(SignUpSuccess, "signUp success")) {
            return ResponseEntity.ok("Company user signup successful");
        }else {
            return ResponseEntity.ok("Company user signup failed");
        }
    }
}
