package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.tech.Tech;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechDTO {
    private Long techId;
    private String techName;
    private String techUrl;

    public Tech toEntity() {
        return Tech.builder()
                .techId(techId)
                .techName(techName)
                .techUrl(techUrl)
                .build();
    }

    public static TechDTO fromEntity(Tech tech) {
        return TechDTO.builder()
                .techId(tech.getTechId())
                .techName(tech.getTechName())
                .techUrl(tech.getTechUrl())
                .build();
    }
}