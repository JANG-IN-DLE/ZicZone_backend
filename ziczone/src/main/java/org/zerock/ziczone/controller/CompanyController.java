package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.ziczone.dto.mypage.CompanyUserDTO;
import org.zerock.ziczone.dto.mypage.PersonalUserDTO;
import org.zerock.ziczone.service.myPage.MyPageService;

import java.util.List;

@RestController
@RequestMapping("/api/companyzone")
@RequiredArgsConstructor
public class CompanyController {

    private final MyPageService myPageService;

    @GetMapping("")
    public ResponseEntity<List<CompanyUserDTO>> companyList() {
        List<CompanyUserDTO> companyUserDTOList = myPageService.getCompanyUserList();
        return ResponseEntity.ok(companyUserDTOList);
    }
}
