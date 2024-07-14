package org.zerock.ziczone.service.myPage;

import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.dto.mypage.ResumeDTO;

import java.util.List;

public interface ResumeService2 {
    ResumeDTO saveResume(ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios);
    ResumeDTO updateResume(Long resumeId, ResumeDTO resumeDTO, MultipartFile resumePhoto, MultipartFile personalState, List<MultipartFile> portfolios);
    void deleteResume(Long resumeId);
    ResumeDTO getResume(Long resumeId);
    ResumeDTO getResumeByUserId(Long userId);
    List<ResumeDTO> getAllResumes();
}
