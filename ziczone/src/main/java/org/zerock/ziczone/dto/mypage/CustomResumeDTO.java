package org.zerock.ziczone.dto.mypage;

import java.time.LocalDateTime;

public class CustomResumeDTO {
    private Long resumeId;
    private String resumeName;
    private String resumeDate;
    private String resumeEmail;
    private String resumePhoto;
    private LocalDateTime resumeCreate;
    private LocalDateTime resumeUpdate;
    private String personalState;
    private Long personalId;
    // 연결된 테이블 작성
    private CertificateDTO certificateDTO;
    private EducationDTO educationDTO;
    private ArchiveDTO archiveDTO;
    private CareerDTO careerDTO;
    private CurriculumDTO curriculumDTO;
    private EtcDTO etcDTO;

}
