package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.application.Etc;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EtcDTO {
    private Long etc_id;
    private String etc_content;
    private String etc_date;

    // DTO to Entity
    public Etc toEntity() {
        return Etc.builder()
                .etcId(this.etc_id)
                .etcContent(this.etc_content)
                .etcDate(this.etc_date)
                .build();
    }

    // Entity to DTO
    public static EtcDTO fromEntity(Etc entity) {
        return EtcDTO.builder()
                .etc_id(entity.getEtcId())
                .etc_content(entity.getEtcContent())
                .etc_date(entity.getEtcDate())
                .build();
    }
}
