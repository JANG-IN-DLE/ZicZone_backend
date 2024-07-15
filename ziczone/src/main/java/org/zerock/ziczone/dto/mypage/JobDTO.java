package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.job.Job;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {
    private Long jobId;
    private String jobName;

    public Job toEntity() {
        return Job.builder()
                .jobId(jobId)
                .jobName(jobName)
                .build();
    }

    public static JobDTO fromEntity(Job job) {
        return JobDTO.builder()
                .jobId(job.getJobId())
                .jobName(job.getJobName())
                .build();
    }
}
