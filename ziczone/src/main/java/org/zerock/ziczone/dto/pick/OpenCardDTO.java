package org.zerock.ziczone.dto.pick;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenCardDTO {

    private Long payHistoryId;
    private Long sellerId;
    private Long buyerId;
    private String payHistoryContent;
    private LocalDateTime payHistoryDate;
}
