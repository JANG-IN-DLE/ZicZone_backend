package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDTO {
    private Long resumeId;
    private String resumeName;
    private String resumeDate;
    private String phoneNum;
    private String resumePhoto;
    private LocalDateTime resumeCreate;
    private LocalDateTime resumeUpdate;
    private String personalState;
    private Long personalId;
}
