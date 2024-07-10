package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.mypage.ResumeDTO;
import org.zerock.ziczone.service.myPage.ResumeService;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class MyPageResumeConroller {

    private final ResumeService resumeService;


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


    /**
     * 지원서 조회 & 생성
     * 지원서 작성 버튼을 클릭 했을 때 Resume 테이블에 userId가 저장되어 있다면 저장된 내용을 가져오고,
     * 조회된 내용이 없을 시 튜플을 생성해서 리턴해준다.(String형식은 빈 문자열으로, 날짜형식은 현재시간으로 값을 넣어준다.)
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ResumeDTO> getResume(@PathVariable Long userId) {
        ResumeDTO resumeDTO = resumeService.getResume(userId);
        return ResponseEntity.ok(resumeDTO);
    }

    /** 지원서 수정
     * 수정을 원할 시 작성하고 싶은 부분만 작성하여 요청을 작성해주면 된다.
     * @param userId
     * @param resumeDTO
     * @return ResponseEntity.ok || status
     */
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateResume(@PathVariable Long userId, @RequestBody ResumeDTO resumeDTO) {
        try {
            resumeService.updateResume(userId, resumeDTO);
            return ResponseEntity.ok("Resume updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update resume");
        }
    }

    /**
     * 지원서 삭제
     * @param userId
     * @return ResponseEntity.ok
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteResume(@PathVariable Long userId) {
        resumeService.deleteResume(userId);
        return ResponseEntity.ok("Resume deleted successfully");
    }
}
