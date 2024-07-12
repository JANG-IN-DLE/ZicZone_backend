package org.zerock.ziczone.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.BennerDTO;
import org.zerock.ziczone.dto.join.CompanyUserJoinDTO;
import org.zerock.ziczone.dto.join.PersonalUserJoinDTO;
import org.zerock.ziczone.dto.mypage.CompanyUserDTO;
import org.zerock.ziczone.service.mainPage.BennerService;
import org.zerock.ziczone.service.mainPage.MainPageService;

import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Log4j2
public class MainPageController {

    private final BennerService bennerService;
    private final MainPageService mainPageService;

    @GetMapping("/companylogolist")
    public ResponseEntity<List<String>> getCompanyLogo() {
        List<String> companyLogoList = mainPageService.companyLogoList();
        return new ResponseEntity<>(companyLogoList, HttpStatus.OK);
    }

    //기업 프로필
    @GetMapping("/companyUser/{id}")
    public ResponseEntity<CompanyUserJoinDTO> getCompanyUserProfile(@PathVariable Long id) {
        CompanyUserJoinDTO companyUser = mainPageService.getCompanyUser(id);
        return new ResponseEntity<>(companyUser, HttpStatus.OK);
    }

    //개인 프로필
    @GetMapping("/personalUser/{id}")
    public ResponseEntity<PersonalUserJoinDTO> getPersonalUserProfile(@PathVariable Long id) {
        PersonalUserJoinDTO personalUser = mainPageService.getPersonalUser(id);
        return new ResponseEntity<>(personalUser, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BennerDTO>> getAllBenners(){
        List<BennerDTO> bennerDTOS = bennerService.getAllBenners();
        return new ResponseEntity<>(bennerDTOS, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<BennerDTO> getBennerDTOById(@PathVariable Long id){
        BennerDTO bennerDTO = bennerService.getBenner(id);
        return new ResponseEntity<>(bennerDTO, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<BennerDTO> createBenner(@RequestBody BennerDTO bennerDTO) {
        log.info(bennerDTO.toString());
        BennerDTO createBenner = bennerService.createBenner(bennerDTO);
        return new ResponseEntity<>(createBenner, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BennerDTO> updateBenner(@PathVariable Long id,@RequestBody BennerDTO bennerDTO) {
        BennerDTO updateBenner = bennerService.updateBenner(id, bennerDTO);
        return new ResponseEntity<>(updateBenner, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBenner(@PathVariable Long id){
        bennerService.deleteBenner(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
