package org.zerock.ziczone.dto.mypage;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("etcId")
    private Long etc_id;
    @JsonProperty("etcContent")
    private String etc_content;
    @JsonProperty("etcDate")
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
