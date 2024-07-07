package org.zerock.ziczone.dto.pick;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.Gender;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickPersonalDetailDTO {
    private Long userId;
    private Long companyId;
    private Long personalId;

    private String userName;
    private String userIntro;
    private Gender gender;
    private String personalCareer;

    private String techUrl;
    private String techName;
    private String jobName;

    private Long payHistoryId;
}
