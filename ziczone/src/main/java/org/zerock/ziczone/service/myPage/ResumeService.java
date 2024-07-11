package org.zerock.ziczone.service.myPage;

import org.zerock.ziczone.dto.mypage.ResumeDTO;

import java.util.Optional;

public interface ResumeService {
    // 지원서 생성
    public ResumeDTO createResume(ResumeDTO resumeDTO, Long userId);
    // 지원서 조회
    public ResumeDTO getResume(Long userId);
    // 지원서 수정
    public ResumeDTO updateResume(Long userId, ResumeDTO resumeDTO);
    // 지원서 삭제
    public void deleteResume(Long userId);

}
