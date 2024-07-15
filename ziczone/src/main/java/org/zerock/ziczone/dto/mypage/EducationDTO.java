package org.zerock.ziczone.dto.mypage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.application.Education;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {
    @JsonProperty("eduId")
    private Long edu_id;
    private String edu;
    private String credit;
    @JsonProperty("eduDate")
    private String edu_date;

    // DTO to Entity
    public Education toEntity() {
        return Education.builder()
                .eduId(this.edu_id)
                .edu(this.edu)
                .credit(this.credit)
                .eduDate(this.edu_date)
                .build();
    }

    // Entity to DTO
    public static EducationDTO fromEntity(Education entity) {
        return EducationDTO.builder()
                .edu_id(entity.getEduId())
                .edu(entity.getEdu())
                .credit(entity.getCredit())
                .edu_date(entity.getEduDate())
                .build();
    }
}
