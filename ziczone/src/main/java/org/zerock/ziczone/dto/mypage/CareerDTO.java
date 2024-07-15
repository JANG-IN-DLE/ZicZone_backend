package org.zerock.ziczone.dto.mypage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.application.Career;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CareerDTO {

    @JsonProperty("careerId")
    private Long career_id;
    @JsonProperty("careerName")
    private String career_name;
    @JsonProperty("careerJob")
    private String career_job;
    @JsonProperty("careerPosition")
    private String career_position;
    @JsonProperty("careerDate")
    private String career_date;

    // DTO to Entity
    public Career toEntity() {
        return Career.builder()
                .careerId(this.career_id)
                .careerName(this.career_name)
                .careerJob(this.career_job)
                .careerPosition(this.career_position)
                .careerDate(this.career_date)
                .build();
    }

    // Entity to DTO
    public static CareerDTO fromEntity(Career entity) {
        return CareerDTO.builder()
                .career_id(entity.getCareerId())
                .career_name(entity.getCareerName())
                .career_job(entity.getCareerJob())
                .career_position(entity.getCareerPosition())
                .career_date(entity.getCareerDate())
                .build();
    }
}
