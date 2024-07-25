package org.zerock.ziczone.service.myPage;

import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.dto.mypage.ResumeDTO;

import java.util.List;

public interface ResumeService {
    
    // 지원서 저장
    ResumeDTO saveResume(ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios);
    // 지원서 수정
    void updateResume(Long resumeId, ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios);
    // 지원서 삭제
    void deleteResume(Long userId);
    // 지원서 조회( 이력서Id )
    ResumeDTO getResume(Long resumeId);
    // 지원서 조회 (userId)
    ResumeDTO getResumeByUserId(Long userId);
    // 모든 지원서 조회
    List<ResumeDTO> getAllResumes();

    boolean ResumeExist(Long userId);

    // 포트폴리오 삭제
    void deletePortfolio(Long portfolioId);
    // 사진 삭제
    void deleteResumePhoto(Long resumeId);
    // 자소서 삭제
    void deleteResumePersonalState(Long resumeId);
}
