package org.zerock.ziczone.dto.Alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestAlarmDTO {
    private String type;
    private Long senderId;
    private Long receiverId;
}
