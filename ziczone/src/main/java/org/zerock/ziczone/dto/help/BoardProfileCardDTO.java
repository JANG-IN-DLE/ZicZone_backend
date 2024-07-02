package org.zerock.ziczone.dto.help;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.Gender;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardProfileCardDTO {
    private Long userId;

    private Long personalId;

    private String jobName;

    private Gender gender;

    private String userName;

    private String personalCareer;

    private Integer corrPoint;

    private String userIntro;

    private String techName;
}
