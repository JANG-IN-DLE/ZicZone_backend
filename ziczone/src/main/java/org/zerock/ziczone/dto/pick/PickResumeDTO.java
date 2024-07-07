package org.zerock.ziczone.dto.pick;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickResumeDTO {

//    개인 정보
    private Long personalId;
    private Long resumeId;
    private String resumeName;
    private String resumeDate;
    private String resumeEmail;
    private String phoneNum;
    private String resumePhoto;
    private String personalState;
    private String jobName;
    private String techUrl;
    private String techName;
//    학력
    private List<String> educations;
//    경력
    private List<String> careers;
//    교육
    private List<String> curriculums;
//    자격증
    private List<String> certificates;
//    기타
    private List<String> etcs;
//    아카이브
    private List<String> archives;
//    포트폴리오
    private List<String> portfolios;
}
