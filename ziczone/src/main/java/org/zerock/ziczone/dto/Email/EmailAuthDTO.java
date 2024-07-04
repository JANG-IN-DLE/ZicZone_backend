package org.zerock.ziczone.dto.Email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailAuthDTO {
    private String email;
    private String code;
    private LocalDateTime createTime;
}