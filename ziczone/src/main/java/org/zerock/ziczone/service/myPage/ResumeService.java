package org.zerock.ziczone.service.myPage;

import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.dto.mypage.ResumeDTO;

import java.util.Optional;

public interface ResumeService {
    // 지원서 생성
    ResumeDTO createResume(ResumeDTO resumeDTO, Long userId);
    // 지원서 조회
    ResumeDTO getResume(Long userId);
    // 지원서 수정
    ResumeDTO updateResume(Long userId, ResumeDTO resumeDTO, MultipartFile resumePhotoFile);
    // 지원서 삭제
    void deleteResume(Long userId);

}
