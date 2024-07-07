package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayHistoryDTO {
    private Long payHistoryId;
    private Long sellerId;
    private Long buyerId;
    private String berryBucket;
    private String payHistoryContent;
    private LocalDateTime payHistoryDate;
}
