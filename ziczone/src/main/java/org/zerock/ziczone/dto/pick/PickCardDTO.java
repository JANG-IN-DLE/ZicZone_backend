package org.zerock.ziczone.dto.pick;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.Gender;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickCardDTO {

    private Long userId;
    private Long personalId;

    private String userName;
    private String userIntro;
    private Gender gender;
    private String personalCareer;
    // techName이 아니라 techUrl로 수정해야한다.
    private String techUrl;
    private String techName;
    private String jobName;

    // scrap 정보와 pick 정보도 보내줘야한다. List로 가져와야겠다.
    private List<Boolean> scrap;
    private List<Boolean> pick;
    // 어떤 회사가 scrap과 pick했는지도 알아야하니까
    private List<Long> companyId;

    // 결제 여부 알기위해서
    private List<Long> payHistoryId;

    private LocalDateTime resumeUpdate;

}
