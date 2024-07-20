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
    private String resumePhotoUrl; // 이미지 파일 경로 또는 데이터
    private String resumePhotoUuid;
    private String resumePhotoFileName;
    private String resumeEmail;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime resumeCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime resumeUpdate;
    private String personalStateUrl; // 자소서 PDF Url
    private String personalStateUuid; // 자소서 PDF UUID
    private String personalStateFileName; // 자소서 PDF FileName
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
                .resumePhotoUrl(this.resumePhotoUrl)
                .resumePhotoUuid(this.resumePhotoUuid)
                .resumePhotoFileName(this.resumePhotoFileName)
                .resumeCreate(this.resumeCreate)
                .resumeUpdate(this.resumeUpdate)
                .personalStateUrl(this.personalStateUrl)
                .personalStateUuid(this.personalStateUuid)
                .personalStateFileName(this.personalStateFileName)
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
                .resumePhotoUrl(entity.getResumePhotoUrl())
                .resumePhotoUuid(entity.getResumePhotoUuid())
                .resumePhotoFileName(entity.getResumePhotoFileName())
                .resumeCreate(entity.getResumeCreate())
                .resumeUpdate(entity.getResumeUpdate())
                .personalStateUrl(entity.getPersonalStateUrl())
                .personalStateUuid(entity.getPersonalStateUuid())
                .personalStateFileName(entity.getPersonalStateFileName())
                .personalId(entity.getPersonalUser().getPersonalId()) // Assumed getter
                .build();
    }


}
