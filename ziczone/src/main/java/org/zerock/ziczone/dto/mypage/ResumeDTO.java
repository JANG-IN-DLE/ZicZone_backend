package org.zerock.ziczone.dto.mypage;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.application.Resume;
import org.zerock.ziczone.domain.member.PersonalUser;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDTO {
    private Long resumeId;
    private String resumeName;
    private String resumeDate;
    private String phoneNum;
    private String resumePhoto; // 이미지 파일 경로 또는 데이터
    private String resumeEmail;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime resumeCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime resumeUpdate;
    private String personalState; // PDF 파일 경로 또는 데이터
    private Long personalId;
    private ArchiveDTO archive;
    private List<EtcDTO> etcs;
    private List<CurriculumDTO> curriculums;
    private List<CareerDTO> careers;
    private List<EducationDTO> educations;
    private List<CertificateDTO> certificates;
    private List<JobPositionDTO> jobPositions;
    private List<TechStackDTO> techStacks;
    private List<PortfolioDTO> portfolios;

    // DTO to Entity
    public Resume toEntity() {
        return Resume.builder()
                .resumeId(this.resumeId)
                .resumeName(this.resumeName)
                .resumeDate(this.resumeDate)
                .phoneNum(this.phoneNum)
                .resumeEmail(this.resumeEmail)
//                .resumePhoto(this.resumePhoto)
                .resumeCreate(this.resumeCreate)
                .resumeUpdate(this.resumeUpdate)
//                .personalState(this.personalState)
                .personalUser(PersonalUser.builder().personalId(this.personalId).build()) // Assumed constructor
                .build();
    }

    // Entity to DTO
    public static ResumeDTO fromEntity(Resume entity) {
        return ResumeDTO.builder()
                .resumeId(entity.getResumeId())
                .resumeName(entity.getResumeName())
                .resumeDate(entity.getResumeDate())
                .phoneNum(entity.getPhoneNum())
                .resumeEmail(entity.getResumeEmail())
//                .resumePhoto(entity.getResumePhoto())
                .resumeCreate(entity.getResumeCreate())
                .resumeUpdate(entity.getResumeUpdate())
//                .personalState(entity.getPersonalState())
                .personalId(entity.getPersonalUser().getPersonalId()) // Assumed getter
                .build();
    }


}
