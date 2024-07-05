package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AggregatedDataDTO {
    private List<PersonalUserDTO> personalUsers;
    private List<ResumeDTO> resumes;
    private List<JobPositionDTO> jobs;
    private List<TechStackDTO> techs;
}
