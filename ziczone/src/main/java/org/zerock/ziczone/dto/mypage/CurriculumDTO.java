package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.application.Curriculum;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CurriculumDTO {
    private Long curri_id;
    private String curri_content;
    private String curri_company;
    private String curri_date;

    // DTO to Entity
    public Curriculum toEntity() {
        return Curriculum.builder()
                .curriId(this.curri_id)
                .curriContent(this.curri_content)
                .curriCompany(this.curri_company)
                .curriDate(this.curri_date)
                .build();
    }

    // Entity to DTO
    public static CurriculumDTO fromEntity(Curriculum entity) {
        return CurriculumDTO.builder()
                .curri_id(entity.getCurriId())
                .curri_content(entity.getCurriContent())
                .curri_company(entity.getCurriCompany())
                .curri_date(entity.getCurriDate())
                .build();
    }
}
