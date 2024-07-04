package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalUserPointDTO {
    private Long userId;
    private String userName;
    private Long remainingPoints;
}
