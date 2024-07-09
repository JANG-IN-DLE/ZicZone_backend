package org.zerock.ziczone.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.dto.join.CompanyUserDTO;
import org.zerock.ziczone.dto.join.PersonalUserDTO;
import org.zerock.ziczone.dto.join.TechDTO;
import org.zerock.ziczone.service.join.JoinService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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
    public ResponseEntity<String> personalUserSignup(@RequestBody PersonalUserDTO personalUserDTO) {
        String SignUpSuccess = joinService.personalSignUp(personalUserDTO);

        if(Objects.equals(SignUpSuccess, "signUp success")) {
            return ResponseEntity.ok("Personal user signup successful");
        }else {
            return ResponseEntity.ok("Personal user signup failed");
        }
    }

    @PostMapping(value="/company", consumes = "multipart/form-data")
    public ResponseEntity<String> companyUserSignUp(@RequestParam(value = "companyLogo") MultipartFile companyLogo,
                                                    @RequestParam("companyUserDTO") String companyUserDTOJson) {

        String bucketName = "ziczone-bucket"; //버킷이름
        String folderName = "CompanyLogo/"; //로고를 저장할 폴더 이름
        String SignUpSuccess;

        // companyUserDTOJson을 CompanyUserDTO 객체로 변환
        CompanyUserDTO companyUserDTO;
        try {
            companyUserDTO = new ObjectMapper().readValue(companyUserDTOJson, CompanyUserDTO.class);
        } catch (IOException e) {
            log.error("Failed to parse companyUserDTO", e);
            return ResponseEntity.badRequest().body("Invalid JSON data");
        }

        //저장될 객체의 이름
        String objectName = folderName + companyUserDTO.getUserName();

        //Amazon S3에 파일을 업로드
        try {
            //ObjectMetadata 객체 생성(파일의 크기, 콘텐츠 유형 등등을 저장)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(companyLogo.getSize()); //크기

            //S3에 파일을 업로드하기 위한 모든 필요한 정보를 포함(버킷이름, 저장될 객체의 이름, 업로드할 파일의 입력스트림, 파일의 메타데이타)
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, companyLogo.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead); // 권한 설정
            amazonS3.putObject(putObjectRequest); //파일 업로드

            // 업로드 된 파일의 URL 저장
            String fileUrl = amazonS3.getUrl(bucketName, objectName).toString();

            //파일의 URL을 DTO에 설정
            companyUserDTO.setCompanyLogo(fileUrl);

            SignUpSuccess = joinService.companyJoin(companyUserDTO);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 회원가입 결과에 따른 응답
        if(Objects.equals(SignUpSuccess, "signUp success")) {
            return ResponseEntity.ok("Company user signup successful");
        }else {
            return ResponseEntity.ok("Company user signup failed");
        }
    }
}
