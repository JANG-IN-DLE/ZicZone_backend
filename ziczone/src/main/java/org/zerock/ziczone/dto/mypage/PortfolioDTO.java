package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.application.Portfolio;
import org.zerock.ziczone.domain.application.Resume;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDTO {
    private Long portId;
    private String portFile;
    private Long resumeId; // Resume ID to link to Resume entity

    // DTO to Entity
    public Portfolio toEntity() {
        return Portfolio.builder()
                .portId(this.portId)
                .portFile(this.portFile)
                .resume(Resume.builder().resumeId(this.resumeId).build())
                .build();
    }

    // Entity to DTO
    public static PortfolioDTO fromEntity(Portfolio entity) {
        return PortfolioDTO.builder()
                .portId(entity.getPortId())
                .portFile(entity.getPortFile())
                .resumeId(entity.getResume() != null ? entity.getResume().getResumeId() : null)
                .build();
    }
}
