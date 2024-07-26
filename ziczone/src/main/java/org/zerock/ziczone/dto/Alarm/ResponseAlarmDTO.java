package org.zerock.ziczone.dto.Alarm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAlarmDTO {
    private String Type;
    private String sender;
    private String receiver;
    private Integer getBerry;
    private LocalDateTime alarmCreate;
    private Boolean readOrNot;
}
