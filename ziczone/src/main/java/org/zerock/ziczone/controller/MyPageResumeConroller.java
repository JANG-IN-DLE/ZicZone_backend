package org.zerock.ziczone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.member.User;
import org.zerock.ziczone.dto.mypage.ResumeDTO;
import org.zerock.ziczone.repository.application.ResumeRepository;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.service.myPage.ResumeService2;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/personal/resumes")
@RequiredArgsConstructor
public class MyPageResumeConroller {

    private final ResumeService2 resumeService;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;


//    /**
//     * 지원서 생성
//     * 지원서 조회시 생성하고 있어서 사용 X
//     * @param userId
//     * @param resumeDTO
//     * @return
//     */
//    @PostMapping("/{userId}")
//    public ResponseEntity<String> createResume(@PathVariable Long userId, @RequestBody ResumeDTO resumeDTO) {
//        resumeService.createResume(resumeDTO, userId);
//        return ResponseEntity.ok("Resume created successfully");
//    }

    ///*/---------------------------------------------------------------------------------------
//    /**
//     * 지원서 조회 & 생성
//     * 지원서 작성 버튼을 클릭 했을 때 Resume 테이블에 userId가 저장되어 있다면 저장된 내용을 가져오고,
//     * 조회된 내용이 없을 시 튜플을 생성해서 리턴해준다.(String형식은 빈 문자열으로, 날짜형식은 현재시간으로 값을 넣어준다.)
//     * @param userId
//     * @return
//     */
//    @GetMapping("/{userId}")
//    public ResponseEntity<ResumeDTO> getResume(@PathVariable Long userId) {
//        ResumeDTO resumeDTO = resumeService.getResume(userId);
//        return ResponseEntity.ok(resumeDTO);
//    }
//
//    /** 지원서 수정
//     * 수정을 원할 시 작성하고 싶은 부분만 작성하여 요청을 작성해주면 된다.
//     * @param userId
//     * @param resumeDTO
//     * @return ResponseEntity.ok || status
//     */
//    @PutMapping("/{userId}")
//    public ResponseEntity<String> updateResume(@PathVariable Long userId,
//                                               @RequestBody ResumeDTO resumeDTO,
//                                               @RequestPart(value = "resumePhoto", required = false) MultipartFile resumePhotoFile) {
//        try {
//            ResumeDTO updatedResume = resumeService.updateResume(userId, resumeDTO, resumePhotoFile);
//            return ResponseEntity.ok("Resume updated successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Failed to update resume");
//        }
//    }
///*
//지원서 수정시 파일로 들어 갈 수 있는 것들은 증명사진, 포트폴리오, 자소서와 같은 3개가 있다. 그렇다면 요청을 받을 때
// */
//    /**
//     * 지원서 삭제
//     * @param userId
//     * @return ResponseEntity.ok
//     */
//    @DeleteMapping("/{userId}")
//    public ResponseEntity<String> deleteResume(@PathVariable Long userId) {
//        resumeService.deleteResume(userId);
//        return ResponseEntity.ok("Resume deleted successfully");
//    }

    ///*/---------------------------------------------------------------------------------------

//    /**
//     * 사용자의 이력서를 조회하거나 새로 생성합니다.
//     * @param userId 사용자의 ID
//     * @return 조회된 또는 생성된 이력서 정보
//     */
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<ResumeDTO> getOrCreateResume(@PathVariable Long userId) {
//        ResumeDTO resume = resumeService.getResumeByUserId(userId);
//        if (resume == null) {
//            // 사용자가 처음 이력서를 생성하는 경우
//            resume = ResumeDTO.builder().personalId(userId).build();
//        }
//        return ResponseEntity.ok(resume);
//    }
//
//    /**
//     * 새로운 이력서를 생성합니다.
//     * @param resumeDTO 생성할 이력서 정보
//     * @param resumePhoto 지원서 사진 파일
//     * @param personalState 자기소개서 파일
//     * @param portfolios 포트폴리오 파일들
//     * @return 생성된 이력서 정보
//     */
//    @PostMapping("/create")
//    public ResponseEntity<ResumeDTO> createResume(
//            @RequestPart ResumeDTO resumeDTO,
//            @RequestPart MultipartFile resumePhoto,
//            @RequestPart MultipartFile personalState,
//            @RequestPart List<MultipartFile> portfolios) {
//        ResumeDTO createdResume = resumeService.saveResume(resumeDTO, resumePhoto, personalState, portfolios);
//        return ResponseEntity.ok(createdResume);
//    }
//
//    /**
//     * 기존 이력서를 업데이트합니다.
//     * @param resumeId 업데이트할 이력서 ID
//     * @param resumeDTO 업데이트할 이력서 정보
//     * @param resumePhoto 지원서 사진 파일
//     * @param personalState 자기소개서 파일
//     * @param portfolios 포트폴리오 파일들
//     * @return 업데이트된 이력서 정보
//     */
//    @PutMapping("/{resumeId}")
//    public ResponseEntity<ResumeDTO> updateResume(
//            @PathVariable Long resumeId,
//            @RequestPart ResumeDTO resumeDTO,
//            @RequestPart MultipartFile resumePhoto,
//            @RequestPart MultipartFile personalState,
//            @RequestPart List<MultipartFile> portfolios) {
//        ResumeDTO updatedResume = resumeService.updateResume(resumeId, resumeDTO, resumePhoto, personalState, portfolios);
//        return ResponseEntity.ok(updatedResume);
//    }
//
//    /**
//     * 이력서를 삭제합니다.
//     * @param resumeId 삭제할 이력서 ID
//     * @return NoContent 응답
//     */
//    @DeleteMapping("/{resumeId}")
//    public ResponseEntity<Void> deleteResume(@PathVariable Long resumeId) {
//        resumeService.deleteResume(resumeId);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * 특정 이력서를 조회합니다.
//     * @param resumeId 조회할 이력서 ID
//     * @return 조회된 이력서 정보
//     */
//    @GetMapping("/{resumeId}")
//    public ResponseEntity<ResumeDTO> getResume(@PathVariable Long resumeId) {
//        ResumeDTO resume = resumeService.getResume(resumeId);
//        return ResponseEntity.ok(resume);
//    }
//
//    /**
//     * 모든 이력서를 조회합니다.
//     * @return 모든 이력서 정보 리스트
//     */
//    @GetMapping
//    public ResponseEntity<List<ResumeDTO>> getAllResumes() {
//        List<ResumeDTO> resumes = resumeService.getAllResumes();
//        return ResponseEntity.ok(resumes);
//    }




    /*
    -----------
     */

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

        log.info("resumeDTOString : {}", resumeDTOString);
        log.info("resumePhoto : {}", resumePhoto);
        log.info("personalState : {}", personalState);
        log.info("portfolios : {}", portfolios);

        ResumeDTO resumeDTO = convertJsonToResumeDTO(resumeDTOString);
        resumeDTO.setPersonalId(userId);
        ResumeDTO savedResume = resumeService.saveResume(resumeDTO, resumePhoto, personalState, portfolios);
        return ResponseEntity.ok("Success Create Resume");
    }


    private ResumeDTO convertJsonToResumeDTO(String resumeDTOString) {
        try {
            return objectMapper.readValue(resumeDTOString, ResumeDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
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
//    @PutMapping("/{resumeId}")
//    public ResponseEntity<ResumeDTO> updateResume(
//            @PathVariable Long resumeId,
//            @RequestPart ResumeDTO resumeDTO,
//            @RequestPart MultipartFile resumePhoto,
//            @RequestPart MultipartFile personalState,
//            @RequestPart List<MultipartFile> portfolios) {
//
//        ResumeDTO updatedResume = resumeService.updateResume(resumeId, resumeDTO, resumePhoto, personalState, portfolios);
//        return ResponseEntity.ok(updatedResume);
//    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateResume(
            @PathVariable Long userId,
            @RequestPart("resumeDTO") String resumeDTOString,
            @RequestPart(required = false) MultipartFile resumePhoto,
            @RequestPart(required = false) MultipartFile personalState,
            @RequestPart(required = false) List<MultipartFile> portfolios) {

//        log.info("Received resumeDTO JSON: {}", resumeDTOString);
//        ResumeDTO resumeDTO = convertJsonToResumeDTO(resumeDTOString);
//        log.info("Parsed ResumeDTO: {}", resumeDTO);
//
//        User user = userRepository.findByUserId(userId);
//        if (user == null) {
//            log.error("User not found with userId: {}", userId);
//            throw new IllegalArgumentException("User not found with userId: " + userId);
//        }
//
//        Long personalId = user.getPersonalUser().getPersonalId();
//        log.info("Found personalId: {}", personalId);
//
//        resumeDTO = resumeDTO.toBuilder().personalId(personalId).build();
//
//        // 기존 데이터를 유지하기 위한 로직 추가
//        if (resumeDTO.getResumeId() != null) {
//            ResumeDTO existingResume = resumeService.getResume(resumeDTO.getResumeId());
//            if (existingResume == null) {
//                throw new IllegalArgumentException("Resume not found with resumeId: " + resumeDTO.getResumeId());
//            }
//
//            // 필요한 필드만 업데이트
//            resumeDTO = existingResume.toBuilder()
//                    .resumeId(resumeDTO.getResumeId())
//                    .personalId(resumeDTO.getPersonalId())
//                    .resumeName(resumeDTO.getResumeName())
//                    .resumeDate(resumeDTO.getResumeDate())
//                    .resumeUpdate(resumeDTO.getResumeUpdate())
//                    .phoneNum(resumeDTO.getPhoneNum())
//                    .resumeEmail(resumeDTO.getResumeEmail())
//                    .resumePhoto(resumeDTO.getResumePhoto())
//                    .personalState(resumeDTO.getPersonalState())
//                    .archive(resumeDTO.getArchive())
//                    .curriculums(resumeDTO.getCurriculums())
//                    .careers(resumeDTO.getCareers())
//                    .educations(resumeDTO.getEducations())
//                    .certificates(resumeDTO.getCertificates())
//                    .portfolios(resumeDTO.getPortfolios())
//                    .techStacks(resumeDTO.getTechStacks() != null ? resumeDTO.getTechStacks() : existingResume.getTechStacks())
//                    .jobPositions(resumeDTO.getJobPositions() != null ? resumeDTO.getJobPositions() : existingResume.getJobPositions())
//                    .build();
//        }
//
//        resumeService.updateResume(resumeDTO.getResumeId(), resumeDTO, resumePhoto, personalState, portfolios);
//        return ResponseEntity.ok("Resume create successfully.");
        log.info("[Controller] Received resumeDTO JSON: {}", resumeDTOString);
        ResumeDTO resumeDTO = convertJsonToResumeDTO(resumeDTOString);
        log.info("[Controller] Parsed ResumeDTO: {}", resumeDTO);

        User user = userRepository.findByUserId(userId);
        if (user == null) {
            log.error("User not found with userId: {}", userId);
            throw new IllegalArgumentException("User not found with userId: " + userId);
        }

        Long personalId = user.getPersonalUser().getPersonalId();
        log.info("[Controller] Found personalId: {}", personalId);

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
     * @param userId 조회할 사용자 ID
     * @return 조회된 이력서 정보
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResumeDTO> getResumeByUserId(@PathVariable Long userId) {
        ResumeDTO resumeDTO = resumeService.getResumeByUserId(userId);
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

}
