package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.job.JobPosition;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobPositionDTO {
    private Long userJobId;
    private JobDTO job;

    public JobPosition toEntity() {
        return JobPosition.builder()
                .userJobId(userJobId)
                .job(job.toEntity())
                .build();
    }

    public static JobPositionDTO fromEntity(JobPosition jobPosition) {
        return JobPositionDTO.builder()
                .userJobId(jobPosition.getUserJobId())
                .job(JobDTO.fromEntity(jobPosition.getJob()))
                .build();
    }
}
