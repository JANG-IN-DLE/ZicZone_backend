package org.zerock.ziczone.dto.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechDTO {
    private Long techId;

    private String techUrl;

    private String techName;
}
