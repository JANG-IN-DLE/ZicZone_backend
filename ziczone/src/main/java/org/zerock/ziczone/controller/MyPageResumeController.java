package org.zerock.ziczone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.mypage.ResumeDTO;
import org.zerock.ziczone.repository.member.PersonalUserRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.service.myPage.ResumeService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/personal/resumes")
@RequiredArgsConstructor
public class MyPageResumeController {

    private final ResumeService resumeService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PersonalUserRepository personalUserRepository;

    /**
     * 새로운 이력서를 저장합니다.
     * @param resumePhoto 이력서 사진 파일
     * @param personalState 개인 상태 파일
     * @param portfolios 포트폴리오 파일 목록
     * @return 저장된 이력서 정보
     */
    @PostMapping("/{userId}")
    public ResponseEntity<String> saveResume(
            @PathVariable Long userId,
            @RequestPart("resumeDTO") String resumeDTOString,
            @RequestPart(value = "resumePhoto", required = false) MultipartFile resumePhoto,
            @RequestPart(value = "personalState", required = false) MultipartFile personalState,
            @RequestPart(value = "portfolios", required = false) List<MultipartFile> portfolios) {

        ResumeDTO resumeDTO = convertJsonToResumeDTO(resumeDTOString);
        User user = userRepository.findByUserId(userId);
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);
        resumeDTO.setPersonalId(personalUser.getPersonalId());
        resumeDTO.setResumeName(user.getUserName());
        ResumeDTO savedResume = resumeService.saveResume(resumeDTO, resumePhoto, personalState, portfolios);
        return ResponseEntity.ok("Success Create Resume");
    }

    /**
     * 기존 이력서를 업데이트합니다.
     * @param userId 업데이트할 이력서 ID
     * @param resumeDTOString 이력서 정보
     * @param resumePhoto 이력서 사진 파일
     * @param personalState 개인 상태 파일
     * @param portfolios 포트폴리오 파일 목록
     * @return 업데이트된 이력서 정보
     */

    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateResume(
            @PathVariable Long userId,
            @RequestPart("resumeDTO") String resumeDTOString,
            @RequestPart(required = false) MultipartFile resumePhoto,
            @RequestPart(required = false) MultipartFile personalState,
            @RequestPart(required = false) List<MultipartFile> portfolios) {


        ResumeDTO resumeDTO = convertJsonToResumeDTO(resumeDTOString);

        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with userId: " + userId);
        }

        Long personalId = user.getPersonalUser().getPersonalId();

        resumeDTO = resumeDTO.toBuilder().personalId(personalId).build();

        resumeService.updateResume(resumeDTO.getResumeId(), resumeDTO, resumePhoto, personalState, portfolios);
        return ResponseEntity.ok("Resume updated successfully.");
    }

    /**
     * 이력서를 삭제합니다.
     * @param resumeId 삭제할 이력서 ID
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<String> deleteResume(@PathVariable Long resumeId) {
        resumeService.deleteResume(resumeId);
        return ResponseEntity.ok("Resume deleted successfully.");
    }

    /**
     * 특정 ID의 이력서를 조회합니다.
     * @param resumeId 조회할 이력서 ID
     * @return 조회된 이력서 정보
     */
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeDTO> getResume(@PathVariable Long resumeId) {
        ResumeDTO resumeDTO = resumeService.getResume(resumeId);
        return ResponseEntity.ok(resumeDTO);
    }

    /**
     * 특정 사용자 ID의 이력서를 조회합니다.
     * @param userId 조회할 사용자 IDmy
     * @return 조회된 이력서 정보
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResumeDTO> getResumeByUserId(@PathVariable Long userId) {
        User user = userRepository.findByUserId(userId);
        PersonalUser personalUser = personalUserRepository.findByUser_UserId(userId);

        ResumeDTO resumeDTO = resumeService.getResumeByUserId(personalUser.getPersonalId());
        resumeDTO.setResumeName(user.getUserName());
        return ResponseEntity.ok(resumeDTO);
    }

    /**
     * 모든 이력서를 조회합니다.
     * @return 모든 이력서 목록
     */
    @GetMapping
    public ResponseEntity<List<ResumeDTO>> getAllResumes() {
        List<ResumeDTO> resumes = resumeService.getAllResumes();
        return ResponseEntity.ok(resumes);
    }



    private ResumeDTO convertJsonToResumeDTO(String resumeDTOString) {
        try {
            return objectMapper.readValue(resumeDTOString, ResumeDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}
