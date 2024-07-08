package org.zerock.ziczone.dto.pick;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickAndScrapDTO {
    private Long pickId;
    // 0 false, 1 true
    private Boolean pick;
    // 0 false, 1 true
    private Boolean scrap;
    private Long companyId;
    private Long personalId;
}
