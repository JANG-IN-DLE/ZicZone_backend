package org.zerock.ziczone.dto.mypage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalUserDTO {

    private Long personalId;
    private String personalCareer;
    private boolean isPersonalVisible;
    private boolean isCompanyVisible;
    private String gender;
    private UserDTO user;
    private List<ResumeDTO> resumes;
    private List<JobPositionDTO> jobPositions;
    private List<TechStackDTO> techStacks;


}
