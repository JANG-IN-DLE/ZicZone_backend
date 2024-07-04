package org.zerock.ziczone.dto.mypage;

import lombok.*;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PersonalUserDTO {

    private Long personalId;
    private String personalCareer;
    private Boolean isPersonalVisible;
    private Boolean isCompanyVisible;
    private String gender;
    private UserDTO user;
    private List<JobPositionDTO> jobPositions;
    private List<TechStackDTO> techStacks;


}
